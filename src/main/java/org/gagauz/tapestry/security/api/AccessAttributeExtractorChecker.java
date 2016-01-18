package org.gagauz.tapestry.security.api;

import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;

public interface AccessAttributeExtractorChecker {

    AccessAttribute extract(PlasticClass plasticClass);

    AccessAttribute extract(PlasticClass plasticClass, PlasticMethod plasticMethod);

    void check(AccessAttribute attribute);

}
