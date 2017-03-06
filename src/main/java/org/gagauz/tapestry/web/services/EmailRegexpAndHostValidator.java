package org.gagauz.tapestry.web.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.Html5Support;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.validator.AbstractValidator;

/**
 * Add functionality for email domain validation
 *
 */
public class EmailRegexpAndHostValidator extends AbstractValidator<Void, String> {

    private static final Pattern PATTERN = Pattern.compile(
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            Pattern.CASE_INSENSITIVE);

    // Explicit call for constructor here to get ours messages
    public EmailRegexpAndHostValidator(JavaScriptSupport javaScriptSupport, Html5Support html5Support) {
        super(null, String.class, "invalid-email", javaScriptSupport);
    }

    @Override
    public void validate(Field field, Void constraintValue, MessageFormatter formatter, String emailValue)
            throws ValidationException {

        String trimmed = emailValue == null ? null : emailValue.trim();
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException(formatter.format(emailValue));
        }
        try {
            if (null != trimmed) {
                String host = trimmed.substring(trimmed.lastIndexOf('@') + 1);
                InetAddress addr = InetAddress.getByName(host);
                if (addr.isSiteLocalAddress() || addr.isLoopbackAddress()) {
                    throw new ValidationException(formatter.format(emailValue));
                }
            }
            // System.out.println(addr.isAnyLocalAddress());
            // System.out.println(addr.isLinkLocalAddress());
            // System.out.println(addr.isLoopbackAddress());
            // System.out.println(addr.isMCGlobal());
            // System.out.println(addr.isMCLinkLocal());
            // System.out.println(addr.isMCNodeLocal());
            // System.out.println(addr.isMCOrgLocal());
            // System.out.println(addr.isMCSiteLocal());
            // System.out.println(addr.isMulticastAddress());
            // System.out.println(addr);
        } catch (UnknownHostException ex) {
            throw new ValidationException(formatter.format(emailValue));
        }
    }

    @Override
    public void render(Field field, Void constraintValue, MessageFormatter formatter, MarkupWriter writer,
            FormSupport formSupport) {
    }
}
