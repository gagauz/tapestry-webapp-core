package com.xl0e.tapestry.security.impl;

import com.xl0e.tapestry.security.api.Credential;

public class CookieCredentials implements Credential {
    private final String value;

    public CookieCredentials(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
