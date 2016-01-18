package org.gagauz.tapestry.security;

import org.gagauz.tapestry.security.api.AccessAttribute;

public class AccessDeniedException extends RuntimeException {

    private AccessAttribute accessAttribute;

    public AccessDeniedException(AccessAttribute accessAttribute) {
        this.accessAttribute = accessAttribute;
    }

    public AccessAttribute getNeedRoles() {
        return accessAttribute;
    }

}
