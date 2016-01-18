package org.gagauz.tapestry.security;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.gagauz.tapestry.security.api.AuthenticationHandler;
import org.gagauz.tapestry.security.api.Credentials;
import org.gagauz.tapestry.security.api.User;
import org.gagauz.tapestry.security.api.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    public <U extends User> U login(Credentials credentials) {
        U newUser = userProvider.findByCredentials(credentials);
        LoginResult result = null;
        if (null != newUser) {
            UserSet userSet = applicationStateManager.getIfExists(UserSet.class);
            if (null == userSet) {
                userSet = new UserSet();
            } else {
                userSet.remove(newUser);
            }
            userSet.add(newUser);
            applicationStateManager.set(UserSet.class, userSet);
            result = new LoginResult(newUser, credentials);
            //            result.setLastUser(lastUser);
        } else {
            result = new LoginResult(credentials);
        }
        for (AuthenticationHandler handler : handlers) {
            handler.handleLogin(result);
        }

        return newUser;
    }

    public void logout() {

        UserSet userSet = applicationStateManager.getIfExists(UserSet.class);

        for (AuthenticationHandler handler : handlers) {
            for (User user : userSet) {
                handler.handleLogout(user);
            }
        }

        applicationStateManager.set(UserSet.class, null);

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
