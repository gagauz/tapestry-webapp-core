package org.apache.tapestry5.web.pages;

import java.util.Arrays;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Lob;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.FieldTranslator;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Checkbox;
import org.apache.tapestry5.corelib.components.DateField;
import org.apache.tapestry5.corelib.components.PasswordField;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.internal.services.CompositeFieldValidator;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.FieldValidatorSource;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.PropertyEditContext;
import org.apache.tapestry5.services.PropertyOutputContext;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.util.EnumValueEncoder;

public class AppPropertyBlocks {

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "translate=prop:textFieldTranslator", "validate=prop:textFieldValidator",
            "clientId=prop:context.propertyId", "annotationProvider=context" })
    private TextField textField;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "translate=prop:textFieldTranslator", "validate=prop:textFieldValidator",
            "clientId=prop:context.propertyId", "annotationProvider=context" })
    private PasswordField passwordField;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "translate=prop:textAreaTranslator", "validate=prop:textAreaValidator", "clientId=prop:context.propertyId",
            "annotationProvider=context" })
    private TextArea textArea;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "translate=prop:bigDecimalTranslator", "validate=prop:bigDecimalValidator",
            "clientId=prop:context.propertyId", "annotationProvider=context" })
    private TextField bigDecimalField;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label", "model=prop:commonEntityModel",
            "validate=prop:commonEntityValidator", "clientId=prop:context.propertyId", "annotationProvider=context" })
    private Select commonEntity;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "clientId=prop:context.propertyid", "validate=prop:dateFieldValidator", "ensureClientIdUnique=true" })
    private DateField dateField;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "clientId=prop:context.propertyid", "validate=prop:calendarFieldValidator", "ensureClientIdUnique=true" })
    private DateField calendarField;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "clientId=prop:context.propertyid", "validate=prop:checkboxFieldValidator", "ensureClientIdUnique=true" })
    private Checkbox checkboxField;

    @Inject
    private TypeCoercer typeCoercer;

    @Inject
    private FieldValidatorSource fieldValidatorSource;

    @Inject
    private SelectModelFactory selectModelFactory;

    @Inject
    @Symbol(SymbolConstants.FORM_GROUP_LABEL_CSS_CLASS)
    @Property(write = false)
    private String labelClass;

    @Inject
    @Symbol(SymbolConstants.FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_CSS_CLASS)
    @Property(write = false)
    private String inputWrapperClass;

    @Environmental
    private FormSupport formSupport;

    @Environmental
    private PropertyEditContext context;

    @Environmental
    private PropertyOutputContext outputContext;

    public PropertyEditContext getContext() {
        return context;
    }

    public PropertyOutputContext getOutputContext() {
        return outputContext;
    }

    public FieldTranslator<?> getTextFieldTranslator() {
        return context.getTranslator(textField);
    }

    public FieldValidator<?> getTextFieldValidator() {
        return createValidator(textField);
    }

    public FieldTranslator<?> getBigDecimalTranslator() {
        return context.getTranslator(bigDecimalField);
    }

    public FieldValidator<?> getBigDecimalValidator() {
        return createValidator(bigDecimalField);
    }

    public FieldTranslator<?> getTextAreaTranslator() {
        return context.getTranslator(textArea);
    }

    public FieldValidator<?> getTextAreaValidator() {
        return createValidator(textArea);
    }

    public FieldValidator<?> getCommonEntityValidator() {
        return createValidator(commonEntity);
    }

    public FieldValidator<?> getDateFieldValidator() {
        return createValidator(dateField);
    }

    public FieldValidator<?> getCalendarFieldValidator() {
        return createValidator(calendarField);
    }

    public FieldValidator<?> getCheckboxFieldValidator() {
        return createValidator(checkboxField);
    }

    /**
     * Provide a value encoder for an enum type.
     */
    @SuppressWarnings("unchecked")
    public ValueEncoder<?> getValueEncoderForProperty() {
        return new EnumValueEncoder<>(typeCoercer, context.getPropertyType());
    }

    public SelectModel getCommonEntityModel() {
        return selectModelFactory.create(Collections.emptyList());
    }

    public boolean isLong() {
        Column column = getContext().getAnnotation(Column.class);
        Lob lob = getContext().getAnnotation(Lob.class);
        return (null != column && column.length() > 255) || null != lob;
    }

    public boolean isPassword() {
        final String fieldId = context.getPropertyId();
        return fieldId.toLowerCase().contains("password")
                || context.getContainerMessages().get(fieldId + "-fieldType").equalsIgnoreCase("password");
    }

    public boolean isDisabled() {
        final String fieldId = context.getPropertyId();
        return context.getContainerMessages().get(fieldId + "-disabled").equals("true");
    }

    protected FieldValidator<?> createValidator(Field field) {
        final String formId = formSupport.getFormValidationId();
        final String fieldId = context.getPropertyId();
        String validators = getMessageIfExists(formId + '-' + fieldId + "-validate");
        if (null == validators) {
            validators = getMessageIfExists(fieldId + "-validate");
        }
        if (null != validators) {
            FieldValidator<?> validator = fieldValidatorSource.createValidators(field, validators);
            return new CompositeFieldValidator(Arrays.asList(validator, context.getValidator(field)));
        }
        return context.getValidator(field);
    }

    protected String getMessageIfExists(final String code) {
        if (context.getContainerMessages().contains(code)) {
            return context.getContainerMessages().get(code);
        }
        return null;
    }

    public boolean isShowPlaceholder() {
        return true;
    }

    public String getPlaceholder() {
        return isShowPlaceholder() ? getContext().getLabel() : "";
    }
}
