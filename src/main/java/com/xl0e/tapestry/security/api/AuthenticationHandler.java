package com.xl0e.tapestry.security.api;

import com.xl0e.tapestry.security.LoginResult;

public interface AuthenticationHandler {
    void handleLogin(LoginResult result);

    void handleLogout(Principal user);
}
