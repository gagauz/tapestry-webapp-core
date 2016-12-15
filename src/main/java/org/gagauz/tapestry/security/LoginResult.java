package org.gagauz.tapestry.security;

import org.gagauz.tapestry.security.api.Credential;
import org.gagauz.tapestry.security.api.Principal;

public class LoginResult {
    private final Principal user;
    private final boolean success;
    private final Credential credentials;
    private Principal lastUser;

    public LoginResult(Principal user, Credential credentials) {
        this.user = user;
        this.credentials = credentials;
        this.success = true;
    }

    public LoginResult(Credential credentials) {
        this.user = null;
        this.credentials = credentials;
        this.success = false;
    }

    public Principal getUser() {
        return user;
    }

    public boolean isSuccess() {
        return success;
    }

    public Credential getCredentials() {
        return credentials;
    }

    public Principal getLastUser() {
        return lastUser;
    }

    public void setLastUser(Principal lastUser) {
        this.lastUser = lastUser;
    }

}
