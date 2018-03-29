package org.apache.tapestry5.web.services.security;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.security.SecurityModule;
import org.apache.tapestry5.security.api.AccessAttributesChecker;
import org.apache.tapestry5.security.api.AccessAttributesExtractor;
import org.apache.tapestry5.security.api.SessionAccessAttributes;

@ImportModule(SecurityModule.class)
public class SecuredAnnotationModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(AccessAttributesChecker.class, SecuredAccessAttributeChecker.class);
        binder.bind(AccessAttributesExtractor.class, SecuredAccessAttributeExtractor.class);
        binder.bind(SessionAccessAttributes.class, SessionAccessAttributesImpl.class);
    }

}
