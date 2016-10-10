package org.gagauz.tapestry.web.pages;

import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Lob;

import org.apache.tapestry5.FieldTranslator;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.FieldValidatorSource;
import org.apache.tapestry5.services.PropertyEditContext;
import org.apache.tapestry5.services.PropertyOutputContext;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.util.EnumValueEncoder;
import org.gagauz.tapestry.web.components.BigDecimalField;

public class AppPropertyBlocks {

    @Component(parameters = {"value=context.propertyValue", "label=prop:context.label",
            "translate=prop:textFieldTranslator", "validate=prop:textFieldValidator",
            "clientId=prop:context.propertyId", "annotationProvider=context"})
    private TextField textField;

    @Component(parameters = {"value=context.propertyValue", "label=prop:context.label",
            "translate=prop:textAreaTranslator",
            "validate=prop:textAreaValidator", "clientId=prop:context.propertyId",
    "annotationProvider=context"})
    private TextArea textArea;

    @Component(parameters = {"value=context.propertyValue", "label=prop:context.label",
            "translate=prop:bigDecimalTranslator", "validate=prop:bigDecimalValidator",
            "clientId=prop:context.propertyId", "annotationProvider=context"})
    private BigDecimalField bigDecimalField;

    @Component(parameters = { "value=context.propertyValue", "label=prop:context.label",
            "model=prop:commonEntityModel", "validate=prop:commonEntityValidator",
            "clientId=prop:context.propertyId", "annotationProvider=context" })
    private Select commonEntity;

    @Inject
    private TypeCoercer typeCoercer;

    @Inject
    private FieldValidatorSource fieldValidatorSource;

    @Inject
    private SelectModelFactory selectModelFactory;

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

    public FieldTranslator getTextFieldTranslator() {
        return context.getTranslator(textField);
    }

    public FieldValidator getTextFieldValidator() {
        return context.getValidator(textField);
    }

    public FieldTranslator getBigDecimalTranslator() {
        return context.getTranslator(bigDecimalField);
    }

    public FieldValidator getBigDecimalValidator() {
        return context.getValidator(bigDecimalField);
    }

    public FieldTranslator getTextAreaTranslator() {
        return context.getTranslator(textArea);
    }

    public FieldValidator getTextAreaValidator() {
        return context.getValidator(textArea);
    }

    public FieldValidator getCommonEntityValidator() {
        return context.getValidator(commonEntity);
    }

    /**
     * Provide a value encoder for an enum type.
     */
    @SuppressWarnings("unchecked")
    public ValueEncoder getValueEncoderForProperty() {
        return new EnumValueEncoder(typeCoercer, context.getPropertyType());
    }

    public SelectModel getCommonEntityModel() {
        return selectModelFactory.create(Collections.emptyList());
    }

    //    public FieldValidator getNotNullValidator() {
    //
    //        return fieldValidatorSource.createValidators(field, expression);
    //    }

    public boolean isLong() {
        Column column = getContext().getAnnotation(Column.class);
        Lob lob = getContext().getAnnotation(Lob.class);
        return (null != column && column.length() > 255) || null != lob;
    }
}
