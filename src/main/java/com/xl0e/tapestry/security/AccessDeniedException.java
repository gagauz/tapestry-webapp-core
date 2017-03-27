package com.xl0e.tapestry.security;

import com.xl0e.tapestry.security.api.AccessAttribute;

public class AccessDeniedException extends RuntimeException {

    private AccessAttribute accessAttribute;

    public AccessDeniedException(AccessAttribute accessAttribute) {
        this.accessAttribute = accessAttribute;

    }

    public AccessAttribute getNeedRoles() {
        return accessAttribute;
    }

}
