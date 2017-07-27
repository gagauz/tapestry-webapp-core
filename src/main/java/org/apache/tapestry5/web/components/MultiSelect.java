package org.apache.tapestry5.web.components;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.FieldValidationSupport;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.NullFieldStrategy;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.BeforeRenderTemplate;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Mixin;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractField;
import org.apache.tapestry5.corelib.data.BlankOption;
import org.apache.tapestry5.corelib.mixins.RenderDisabled;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.internal.util.SelectModelRenderer;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ValueEncoderSource;

/**
 *
 * @author mg
 */
public class MultiSelect extends AbstractField {

    protected class Renderer extends SelectModelRenderer {

        public Renderer(MarkupWriter writer, ValueEncoder encoder) {
            super(writer, encoder, true);
        }

        @Override
        public boolean isOptionSelected(OptionModel optionModel, String clientValue) {
            return isSelected(optionModel.getValue());
        }
    }

    @Parameter
    protected ValueEncoder encoder;
    protected ValueEncoder encoderValue;

    @Parameter(defaultPrefix = BindingConstants.NULLFIELDSTRATEGY, value = "default")
    private NullFieldStrategy nulls;

    @Inject
    private ComponentDefaultProvider defaultProvider;

    @Parameter(required = true, allowNull = false)
    protected SelectModel model;

    @Parameter(value = "auto", defaultPrefix = BindingConstants.LITERAL)
    private BlankOption blankOption;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String blankLabel;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "1")
    protected int size;

    @Inject
    @Symbol(SymbolConstants.FORM_FIELD_CSS_CLASS)
    protected String cssClass;

    @Inject
    private Request request;

    @Inject
    protected ComponentResources resources;

    @Environmental
    private ValidationTracker tracker;

    @Parameter(defaultPrefix = BindingConstants.VALIDATE)
    protected FieldValidator<Object> validate;

    @Parameter(required = true, principal = true, autoconnect = true)
    protected Object value;

    @Inject
    private FieldValidationSupport fieldValidationSupport;

    @Mixin
    private RenderDisabled renderDisabled;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    protected boolean isSelected(Object optionValue) {
        return value != null && ((Collection.class.isAssignableFrom(value.getClass()) && ((Collection) value).contains(optionValue)))
                || Objects.equals(value, optionValue);
    }

    @Override
    protected void processSubmission(String controlName) {
        String[] params = request.getParameters(controlName);
        Object submittedValue = null;
        Collection submittedValueCollection = toValue(null == params ? new String[0] : params);
        Class type = resources.getBoundType("value");
        if (null != type) {
            if (Collection.class.isAssignableFrom(type)) {
                submittedValue = submittedValueCollection;
            } else if (!submittedValueCollection.isEmpty()) {
                submittedValue = submittedValueCollection.iterator().next();
            }
        }

        putPropertyNameIntoBeanValidationContext("value");

        try {
            fieldValidationSupport.validate(submittedValue, resources, validate);

            value = submittedValue;
        } catch (ValidationException ex) {
            tracker.recordError(this, ex.getMessage());
        }

        removePropertyNameFromBeanValidationContext();
    }

    protected void afterRender(MarkupWriter writer) {
        writer.end();
    }

    protected void beginRender(MarkupWriter writer) {

        int sizeA = size;
        if (model != null && model.getOptions().size() < size) {
            sizeA = model.getOptions().size();
            if (blankOption != BlankOption.NEVER) {
                sizeA++;
            }
        }
        Element element = writer.element("select", "name", getControlName(), "id", getClientId(), "class", cssClass);
        if (sizeA > 1) {
            element.forceAttributes("multiple", "true");
            element.forceAttributes("size", String.valueOf(sizeA));
        }

        putPropertyNameIntoBeanValidationContext("value");

        validate.render(writer);

        removePropertyNameFromBeanValidationContext();

        resources.renderInformalParameters(writer);

        decorateInsideField();

        // Disabled is via a mixin
    }

    protected Collection toValue(String[] values) {
        return F.flow(values).map(element -> {
            if (StringUtils.isEmpty(element)) {
                element = nulls.replaceFromClient();
            }
            return getEncoder().toValue(element);
        }).toList();
    }

    private ValueEncoder getEncoder() {
        if (null == encoderValue) {
            if (null != model) {
                if (null != model.getOptions()) {
                    for (OptionModel option : model.getOptions()) {
                        if (null != option.getValue()) {
                            encoderValue = valueEncoderSource.getValueEncoder(option.getValue().getClass());
                            break;
                        }
                    }
                }
                if (null != model.getOptionGroups()) {
                    for (OptionGroupModel group : model.getOptionGroups()) {
                        for (OptionModel option : group.getOptions()) {
                            if (null != option.getValue()) {
                                encoderValue = valueEncoderSource.getValueEncoder(option.getValue().getClass());
                                break;
                            }
                        }
                    }
                }
            }
            if (null == encoderValue) {
                Type type = resources.getBoundGenericType("value");
                Class<?> valueClass = null;
                if (null != type && type instanceof ParameterizedType) {
                    try {
                        valueClass = Class.forName(((ParameterizedType) type).getActualTypeArguments()[0].getTypeName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                if (null == valueClass) {
                    valueClass = resources.getBoundType("value");
                }
                encoderValue = valueEncoderSource.getValueEncoder(valueClass);
            }
        }
        return encoderValue;
    }

    ValueEncoder defaultEncoder() {
        return getEncoder();
    }

    Binding defaultValidate() {
        return defaultProvider.defaultValidatorBinding("value", resources);
    }

    Object defaultBlankLabel() {
        Messages containerMessages = resources.getContainerMessages();

        String key = resources.getId() + "-blanklabel";

        if (containerMessages.contains(key)) {
            return containerMessages.get(key);
        }

        return null;
    }

    @BeforeRenderTemplate
    protected void options(final MarkupWriter writer) {

        Renderer renderer = new Renderer(writer, getEncoder());
        if (showBlankOption()) {
            Element el = writer.element("option");
            el.attribute("value", "");
            writer.write(blankLabel);
            writer.end();
        }

        model.visit(renderer);
    }

    @Override
    public boolean isRequired() {
        return validate.isRequired();
    }

    private boolean showBlankOption() {
        switch (blankOption) {
        case ALWAYS:
            return true;

        case NEVER:
            return false;

        default:
            return !isRequired();
        }
    }

    // For testing.

    void setModel(SelectModel model) {
        this.model = model;
        blankOption = BlankOption.NEVER;
    }

    void setValidationTracker(ValidationTracker tracker) {
        this.tracker = tracker;
    }

    void setBlankOption(BlankOption option, String label) {
        blankOption = option;
        blankLabel = label;
    }
}
