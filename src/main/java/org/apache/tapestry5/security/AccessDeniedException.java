package org.apache.tapestry5.security;

import org.apache.tapestry5.security.api.AccessAttribute;

public class AccessDeniedException extends RuntimeException {

    private AccessAttribute accessAttribute;

    public AccessDeniedException(AccessAttribute accessAttribute) {
        this.accessAttribute = accessAttribute;

    }

    public AccessAttribute getNeedRoles() {
        return accessAttribute;
    }

}
