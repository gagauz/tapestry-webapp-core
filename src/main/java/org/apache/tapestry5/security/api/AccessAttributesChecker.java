package org.apache.tapestry5.security.api;

public interface AccessAttributesChecker<A extends AccessAttributes> {

    boolean canAccess(A sessionAttribute, A resourceAttribute);

}
