package org.gagauz.tapestry.security.api;

public interface UserProvider {
    <U extends User, C extends Credentials> U findByCredentials(C credentials);
}
