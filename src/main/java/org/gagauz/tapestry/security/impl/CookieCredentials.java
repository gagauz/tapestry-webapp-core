package org.gagauz.tapestry.security.impl;

import org.gagauz.tapestry.security.api.Credential;

public class CookieCredentials implements Credential {
    private final String value;

    public CookieCredentials(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
