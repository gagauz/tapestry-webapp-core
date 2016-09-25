package org.gagauz.tapestry.security.api;

public interface UserProvider {
    <U extends IUser, C extends Credentials> U findByCredentials(C credentials);

    <U extends IUser, C extends Credentials> C toCredentials(U user, Class<C> clazz);
}
