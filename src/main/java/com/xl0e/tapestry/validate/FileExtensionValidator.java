package com.xl0e.tapestry.validate;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.validator.AbstractValidator;

import java.util.regex.Pattern;

public class FileExtensionValidator extends AbstractValidator<String, Object> {

    public FileExtensionValidator(JavaScriptSupport jsSupport) {
        super(String.class, Object.class, "validate-extension", jsSupport);
    }

    @Override
    public void validate(Field field, String constraintValue, MessageFormatter formatter, Object value) throws ValidationException {
        String str = "(?i)^.*\\.(" + constraintValue.replace(',', '|') + ")$";
        Pattern regex = Pattern.compile(str);

        String string = null;
        if (value instanceof UploadedFile) {
            string = ((UploadedFile) value).getFileName();
        }
        if (value instanceof String) {
            string = (String) value;
        }
        if (null != value && !regex.matcher(string).matches()) {
            throw new ValidationException(formatter.format(field.getLabel(), constraintValue));
        }

    }

    @Override
    public void render(Field field, String constraintValue, MessageFormatter formatter, MarkupWriter writer, FormSupport formSupport) {
        if (null == constraintValue) {
            throw new IllegalStateException();
        }

        if (formSupport.isClientValidationEnabled()) {
            //            javaScriptSupport.require("t5/core/validation");
            //
            //            writer.attributes(
            //                    DataConstants.VALIDATION_ATTRIBUTE, true,
            //                    "data-optionality", "required",
            //                    "data-required-message", buildMessage(formatter, field));
        }
    }
}
