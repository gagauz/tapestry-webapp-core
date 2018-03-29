package org.apache.tapestry5.security.api;

public interface UserProvider<U extends User, C extends Credentials> {
    U findByCredential(C credentials);
}
