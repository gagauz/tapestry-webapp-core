package org.apache.tapestry5.security.impl;

import org.apache.tapestry5.security.api.Credentials;

public class UserAndPassCredentials implements Credentials {
    private final String username;
    private final String password;

    public UserAndPassCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
