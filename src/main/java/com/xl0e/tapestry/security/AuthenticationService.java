package com.xl0e.tapestry.security;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xl0e.tapestry.security.api.AuthenticationHandler;
import com.xl0e.tapestry.security.api.Credential;
import com.xl0e.tapestry.security.api.Principal;
import com.xl0e.tapestry.security.api.UserProvider;

public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Inject
    private UserProvider userProvider;

    @Inject
    private ApplicationStateManager applicationStateManager;

    @Inject
    private List<AuthenticationHandler> handlers;

    @Inject
    private Request request;

    public <P extends Principal, C extends Credential> P login(C credential) {
        LoginResult result = null;
        P newUser = userProvider.findByCredential(credential);
        if (null != newUser) {
            PrincipalStorage userSet = applicationStateManager.getIfExists(PrincipalStorage.class);
            if (null == userSet) {
                userSet = new PrincipalStorage();
            } else {
                userSet.remove(newUser);
            }
            userSet.add(newUser);
            applicationStateManager.set(PrincipalStorage.class, userSet);
            @SuppressWarnings("unchecked")
            Class<P> userClass = (Class<P>) newUser.getClass();
            applicationStateManager.set(userClass, newUser);
            result = new LoginResult(newUser, credential);
        } else {
            result = new LoginResult(credential);
        }
        for (AuthenticationHandler handler : handlers) {
            handler.handleLogin(result);
        }

        return newUser;
    }

    public <P extends Principal> P login(P newUser) {
        LoginResult result = null;
        PrincipalStorage userSet = applicationStateManager.getIfExists(PrincipalStorage.class);
        if (null == userSet) {
            userSet = new PrincipalStorage();
        } else {
            userSet.remove(newUser);
        }
        userSet.add(newUser);
        applicationStateManager.set(PrincipalStorage.class, userSet);
        @SuppressWarnings("unchecked")
        Class<P> userClass = (Class<P>) newUser.getClass();
        applicationStateManager.set(userClass, newUser);
        result = new LoginResult(newUser, null);
        for (AuthenticationHandler handler : handlers) {
            handler.handleLogin(result);
        }

        return newUser;
    }

    public void logout() {

        PrincipalStorage userSet = applicationStateManager.getIfExists(PrincipalStorage.class);

        for (AuthenticationHandler handler : handlers) {
            for (Principal user : userSet) {
                handler.handleLogout(user);
                applicationStateManager.set(user.getClass(), null);
            }
        }

        applicationStateManager.set(PrincipalStorage.class, null);

        Session session = request.getSession(false);

        if (null != session) {
            try {
                session.invalidate();
            } catch (Exception e) {
                log.error("Session invalidate error", e);
            }
        }
    }

}
