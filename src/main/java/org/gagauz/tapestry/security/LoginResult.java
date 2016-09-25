package org.gagauz.tapestry.security;

import org.gagauz.tapestry.security.api.Credentials;
import org.gagauz.tapestry.security.api.IUser;

public class LoginResult {
    private final IUser user;
    private final boolean success;
    private final Credentials credentials;
    private IUser lastUser;

    public LoginResult(IUser user, Credentials credentials) {
        this.user = user;
        this.credentials = credentials;
        this.success = true;
    }

    public LoginResult(Credentials credentials) {
        this.user = null;
        this.credentials = credentials;
        this.success = false;
    }

    public IUser getUser() {
        return user;
    }

    public boolean isSuccess() {
        return success;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public IUser getLastUser() {
        return lastUser;
    }

    public void setLastUser(IUser lastUser) {
        this.lastUser = lastUser;
    }

}
