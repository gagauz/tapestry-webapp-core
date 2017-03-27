package com.xl0e.tapestry.security.api;

import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;

import com.xl0e.tapestry.security.PrincipalStorage;

public interface AccessAttributeExtractorChecker<A extends AccessAttribute> {

    A extract(PlasticClass plasticClass, PlasticMethod plasticMethod);

    boolean check(PrincipalStorage users, A attribute);

}
