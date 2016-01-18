package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.ioc.MessageFormatter;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.Html5Support;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.validator.Email;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Extends tapestry Email validator to add functionality for email domain validation
 *
 * @author Гагауз Михаил
 */
public class EmailRegexpAndHostValidator extends Email {

    // Explicit call for constructor here to get ours messages
    public EmailRegexpAndHostValidator(JavaScriptSupport javaScriptSupport, Html5Support html5Support) {
        super(javaScriptSupport, html5Support);
        //   this.service = service;
    }

    @Override
    public void validate(Field field, Void constraintValue, MessageFormatter formatter, String emailValue) throws ValidationException {

        String trimmed = emailValue == null ? null : emailValue.trim();
        super.validate(field, constraintValue, formatter, emailValue);

        try {
            if (null != trimmed) {
                String host = trimmed.substring(trimmed.lastIndexOf('@') + 1);
                InetAddress addr = InetAddress.getByName(host);
                if (addr.isSiteLocalAddress() || addr.isLoopbackAddress()) {
                    throw new ValidationException(formatter.format(emailValue));
                }
            }
            //            System.out.println(addr.isAnyLocalAddress());
            //            System.out.println(addr.isLinkLocalAddress());
            //            System.out.println(addr.isLoopbackAddress());
            //            System.out.println(addr.isMCGlobal());
            //            System.out.println(addr.isMCLinkLocal());
            //            System.out.println(addr.isMCNodeLocal());
            //            System.out.println(addr.isMCOrgLocal());
            //            System.out.println(addr.isMCSiteLocal());
            //            System.out.println(addr.isMulticastAddress());
            //            System.out.println(addr);
        } catch (UnknownHostException ex) {
            throw new ValidationException(formatter.format(emailValue));
        }
    }

    @Override
    public void render(Field field, Void constraintValue, MessageFormatter formatter, MarkupWriter writer, FormSupport formSupport) {

    }
}
