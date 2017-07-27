package org.apache.tapestry5.security.impl;

import org.apache.tapestry5.security.api.Credential;

public class CookieCredentials implements Credential {
    private final String value;

    public CookieCredentials(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
