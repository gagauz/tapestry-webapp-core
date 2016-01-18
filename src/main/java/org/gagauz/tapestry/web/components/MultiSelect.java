package org.gagauz.tapestry.web.components;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.FieldValidationSupport;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.NullFieldStrategy;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SelectModelVisitor;
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
import org.apache.tapestry5.func.F;
import org.apache.tapestry5.func.Mapper;
import org.apache.tapestry5.internal.util.SelectModelRenderer;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentDefaultProvider;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ValueEncoderSource;

/**
 *
 * @author mg
 */
public class MultiSelect extends AbstractField {

    private final ValueEncoder<Object> emptyEncoder = new ValueEncoder<Object>() {

        @Override
        public String toClient(Object value) {
            return null;
        }

        @Override
        public Object toValue(String clientValue) {
            return null;
        }
    };

    protected class Renderer extends SelectModelRenderer {

        public Renderer(MarkupWriter writer, ValueEncoder encoder) {
            super(writer, encoder, true);
        }

        @Override
        public boolean isOptionSelected(OptionModel optionModel, String clientValue) {
            return isSelected(clientValue);
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
    private Request request;

    @Inject
    protected ComponentResources resources;

    @Environmental
    private ValidationTracker tracker;

    @Parameter(defaultPrefix = BindingConstants.VALIDATE)
    protected FieldValidator<Object> validate;

    @Parameter(required = true, principal = true, autoconnect = true)
    protected Collection value;

    @Inject
    private FieldValidationSupport fieldValidationSupport;

    @Mixin
    private RenderDisabled renderDisabled;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    protected boolean isSelected(String clientValue) {
        return value != null && value.contains(getEncoder().toValue(clientValue));
    }

    @Override
    protected void processSubmission(String controlName) {
        String[] params = request.getParameters(controlName);
        Collection submittedValue = toValue(null == params ? new String[0] : params);

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
        writer.element("select", "name", getControlName(), "id", getClientId(), "multiple", true, "size", sizeA);

        putPropertyNameIntoBeanValidationContext("value");

        validate.render(writer);

        removePropertyNameFromBeanValidationContext();

        resources.renderInformalParameters(writer);

        decorateInsideField();

        // Disabled is via a mixin
    }

    protected Collection toValue(String[] values) {
        return F.flow(values).map(new Mapper<String, Object>() {
            @Override
            public Object map(String element) {
                if (StringUtils.isEmpty(element)) {
                    element = nulls.replaceFromClient();
                }
                return getEncoder().toValue(element);
            }

        }).toList();
    }

    private ValueEncoder getEncoder() {
        if (null == encoderValue) {
            if (null != encoder && emptyEncoder != encoder) {
                return encoderValue = encoder;
            }
            if (null == model || model.getOptions().isEmpty()) {
                return emptyEncoder;
            }
            return encoderValue = valueEncoderSource.getValueEncoder(model.getOptions().get(0).getValue().getClass());
        }
        return encoderValue;
    }

    ValueEncoder defaultEncoder() {
        return emptyEncoder;
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
    protected void options(MarkupWriter writer) {
        if (showBlankOption()) {
            writer.element("option", "value", getEncoder().toClient(null));
            writer.write(blankLabel);
            writer.end();
        }

        SelectModelVisitor renderer = new Renderer(writer, getEncoder());

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

    void setValue(Collection value) {
        this.value = value;
    }

    void setValidationTracker(ValidationTracker tracker) {
        this.tracker = tracker;
    }

    void setBlankOption(BlankOption option, String label) {
        blankOption = option;
        blankLabel = label;
    }
}
