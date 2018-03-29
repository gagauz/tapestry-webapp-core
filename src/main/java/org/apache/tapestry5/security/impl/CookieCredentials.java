package org.apache.tapestry5.security.impl;

import org.apache.tapestry5.security.api.Credentials;

public class CookieCredentials implements Credentials {
    private final String value;

    public CookieCredentials(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
