package org.apache.tapestry5.web.services.security;

import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.security.api.AccessAttributesExtractor;

public class SecuredAccessAttributeExtractor implements AccessAttributesExtractor<SecuredAccessAttributes> {

    @Override
    public SecuredAccessAttributes extract(PlasticClass plasticClass, PlasticMethod plasticMethod) {

        if (null == plasticMethod) {
            Secured annotation = plasticClass.getAnnotation(Secured.class);
            if (null != annotation) {
                return new SecuredAccessAttributes(annotation.value());
            }
            return null;
        }

        Secured annotation = plasticMethod.getAnnotation(Secured.class);
        if (null != annotation) {
            return new SecuredAccessAttributes(annotation.value());
        }
        return null;
    }
}
