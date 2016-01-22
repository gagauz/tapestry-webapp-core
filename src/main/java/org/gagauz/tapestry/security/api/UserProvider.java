package org.gagauz.tapestry.security.api;

public interface UserProvider {
    <U extends User, C extends Credentials> U fromCredentials(C credentials);

    <U extends User, C extends Credentials> C toCredentials(U user, Class<C> clazz);
}
