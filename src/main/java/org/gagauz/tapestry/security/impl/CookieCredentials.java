package org.gagauz.tapestry.security.impl;

import org.gagauz.tapestry.security.api.Credentials;

public class CookieCredentials implements Credentials {
    private final String value;

    public CookieCredentials(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
