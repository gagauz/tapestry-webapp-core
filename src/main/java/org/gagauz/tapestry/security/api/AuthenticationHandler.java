package org.gagauz.tapestry.security.api;

import org.gagauz.tapestry.security.LoginResult;

public interface AuthenticationHandler {
    void handleLogin(LoginResult result);

    void handleLogout(IUser user);
}
