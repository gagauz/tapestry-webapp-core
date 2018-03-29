package org.apache.tapestry5.security.api;

import org.apache.tapestry5.security.LoginResult;

public interface AuthenticationHandler {
    void handleLogin(LoginResult result);

    void handleLogout(AccessAttributes user);
}
