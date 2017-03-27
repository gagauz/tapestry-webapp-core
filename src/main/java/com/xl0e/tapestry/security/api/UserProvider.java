package com.xl0e.tapestry.security.api;

public interface UserProvider {
    <U extends Principal, C extends Credential> U findByCredential(C credentials);

    <U extends Principal, C extends Credential> C toCredentials(U user, Class<C> clazz);
}
