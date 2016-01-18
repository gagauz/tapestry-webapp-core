package org.gagauz.tapestry.validate;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.validator.AbstractValidator;

public class NonLatinCharsValidator extends AbstractValidator<Void, String> {

    public NonLatinCharsValidator(JavaScriptSupport jsSupport) {
        super(null, String.class, "validate-latin", jsSupport);
    }

    @Override
    public void validate(Field field, Void constraintValue, MessageFormatter formatter, String emailValue) throws ValidationException {
        if (!StringUtils.isAsciiPrintable(emailValue)) {
            throw new ValidationException(formatter.format(field.getLabel()));
        }
    }

    @Override
    public void render(Field field, Void constraintValue, MessageFormatter formatter, MarkupWriter writer, FormSupport formSupport) {
        formSupport.addValidation(field, "validate-latin", formatter.format(field.getLabel()), null);
    }
}
