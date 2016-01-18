package org.gagauz.tapestry.security.impl;

import org.gagauz.tapestry.security.api.Credentials;

public class LoginFormCredentials implements Credentials {
    private final String username;
    private final String password;
    private final boolean remember;

    public LoginFormCredentials(String username, String password, boolean remember) {
        this.username = username;
        this.password = password;
        this.remember = remember;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRemember() {
        return remember;
    }
}
