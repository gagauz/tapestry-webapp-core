package org.gagauz.tapestry.security.api;

import java.util.Collection;

public interface User {
    Collection<Role> getRoles();

    Collection<Domain> getDomains();

}
