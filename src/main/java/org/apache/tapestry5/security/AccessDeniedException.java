package org.apache.tapestry5.security;

import org.apache.tapestry5.security.api.AccessAttributes;

public class AccessDeniedException extends RuntimeException {

    private AccessAttributes accessAttribute;

    public AccessDeniedException(AccessAttributes accessAttribute) {
        this.accessAttribute = accessAttribute;

    }

    public AccessAttributes getNeedRoles() {
        return accessAttribute;
    }

}
