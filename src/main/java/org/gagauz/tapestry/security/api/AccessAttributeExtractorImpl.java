package org.gagauz.tapestry.security.api;

import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.gagauz.tapestry.web.services.security.AnnotationAccessAttribute;
import org.gagauz.tapestry.web.services.security.Secured;

public class AccessAttributeExtractorImpl {

    @Override
    public AccessAttribute extract(PlasticClass plasticClass) {
        Secured annotation = plasticClass.getAnnotation(Secured.class);
        if (null != annotation) {
            return new AnnotationAccessAttribute(annotation.value());
        }
        return null;
    }

    @Override
    public AccessAttribute extract(PlasticClass plasticClass, PlasticMethod plasticMethod) {
        Secured annotation = plasticMethod.getAnnotation(Secured.class);
        if (null != annotation) {
            return new AnnotationAccessAttribute(annotation.value());
        }
        return null;
    }

}
