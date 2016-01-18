package org.gagauz.tapestry.security;

import org.gagauz.tapestry.security.api.Credentials;
import org.gagauz.tapestry.security.api.User;

public class LoginResult {
    private final User user;
    private final boolean success;
    private final Credentials credentials;
    private User lastUser;

    public LoginResult(User user, Credentials credentials) {
        this.user = user;
        this.credentials = credentials;
        this.success = true;
    }

    public LoginResult(Credentials credentials) {
        this.user = null;
        this.credentials = credentials;
        this.success = false;
    }

    public User getUser() {
        return user;
    }

    public boolean isSuccess() {
        return success;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public User getLastUser() {
        return lastUser;
    }

    public void setLastUser(User lastUser) {
        this.lastUser = lastUser;
    }

}
