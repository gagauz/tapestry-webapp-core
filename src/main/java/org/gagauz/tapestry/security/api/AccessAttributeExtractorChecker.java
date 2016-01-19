package org.gagauz.tapestry.security.api;

import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.gagauz.tapestry.security.AccessDeniedException;

public interface AccessAttributeExtractorChecker<A extends AccessAttribute> {

    A extract(PlasticClass plasticClass);

    A extract(PlasticClass plasticClass, PlasticMethod plasticMethod);

    void check(A attribute) throws AccessDeniedException;

}
