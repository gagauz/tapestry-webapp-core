package org.apache.tapestry5.security.api;

public interface SessionAccessAttributes<A extends AccessAttributes> {
    AccessAttributes getSessionAttributes();

    A setSessionAttributes(A newUser);
}
