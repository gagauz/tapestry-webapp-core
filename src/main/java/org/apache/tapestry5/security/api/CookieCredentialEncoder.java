package org.apache.tapestry5.security.api;

import org.apache.tapestry5.security.impl.CookieCredentials;

public interface CookieCredentialEncoder<U extends User> {
    CookieCredentials encode(U user);
}
