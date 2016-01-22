package org.gagauz.tapestry.security.api;

import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.gagauz.tapestry.security.UserSet;

public interface AccessAttributeExtractorChecker {

    <A extends AccessAttribute> A extract(PlasticClass plasticClass, PlasticMethod plasticMethod);

    <A extends AccessAttribute> boolean check(UserSet users, A attribute);

}
