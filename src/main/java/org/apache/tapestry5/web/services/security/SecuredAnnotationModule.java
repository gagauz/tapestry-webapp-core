package org.apache.tapestry5.web.services.security;

import java.util.Collection;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.security.SecurityModule;
import org.apache.tapestry5.security.api.AccessAttributes;
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Contribute(TypeCoercer.class)
    public static void contributeTypeCoercer(Configuration<CoercionTuple> configuration) {
        configuration.add(CoercionTuple.create(Collection.class, AccessAttributes.class, input -> new SecuredAccessAttributes(input)));
        configuration.add(CoercionTuple.create(String.class, AccessAttributes.class, input -> new SecuredAccessAttributes(input)));

    }
}
