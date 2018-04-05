package org.apache.tapestry5.security.impl;

import org.apache.tapestry5.security.api.Credentials;

public class UsernamePasswordCredentials implements Credentials {
    private final String username;
    private final String password;
    private final boolean rememberMe;

    public UsernamePasswordCredentials(String username, String password, boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }
}
