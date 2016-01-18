package org.gagauz.tapestry.validate;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.validator.AbstractValidator;

import java.util.regex.Pattern;

public class FileExtensionValidator extends AbstractValidator<Void, Object> {

    private final Pattern regex;
    private final String exts;

    public FileExtensionValidator(JavaScriptSupport jsSupport, String... extensions) {
        super(null, Object.class, "validate-extension", jsSupport);
        exts = StringUtils.join(extensions, ',');
        String str = "(?i)^.*\\.(" + StringUtils.join(extensions, '|') + ")$";
        regex = Pattern.compile(str);
    }

    @Override
    public void validate(Field field, Void constraintValue, MessageFormatter formatter, Object value) throws ValidationException {
        String string = null;
        if (value instanceof UploadedFile) {
            string = ((UploadedFile) value).getFileName();
        }
        if (value instanceof String) {
            string = (String) value;
        }
        if (null != value && !regex.matcher(string).matches()) {
            throw new ValidationException(formatter.format(field.getLabel(), exts));
        }
    }

    public static void main(String[] args) {
        FileExtensionValidator v = new FileExtensionValidator(null, "flv", "jpg");
        System.out.println(v.regex.matcher("asdgdsdfgdfg.fgdf").matches());
        System.out.println(v.regex.matcher("asdgdsdfgdfg.flv").matches());
        System.out.println(v.regex.matcher("asdgdsdfgdfg").matches());
        System.out.println(v.regex.matcher("asdgdsdfgdfg.jpg").matches());
        System.out.println(v.regex.matcher("asdgdsdfgdfg.JPG").matches());
    }

    @Override
    public void render(Field field, Void constraintValue, MessageFormatter formatter, MarkupWriter writer, FormSupport formSupport) {
        formSupport.addValidation(field, "validate-extension", formatter.format(field.getLabel(), exts), null);
    }
}
