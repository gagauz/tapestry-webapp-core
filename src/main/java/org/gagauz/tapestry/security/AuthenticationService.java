package org.gagauz.tapestry.security;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.gagauz.tapestry.security.api.AuthenticationHandler;
import org.gagauz.tapestry.security.api.Credentials;
import org.gagauz.tapestry.security.api.IUser;
import org.gagauz.tapestry.security.api.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public <USER extends IUser, CREDENTIALS extends Credentials> USER login(CREDENTIALS credentials) {
        LoginResult result = null;
        USER newUser = userProvider.findByCredentials(credentials);
        if (null != newUser) {
            UserSet userSet = applicationStateManager.getIfExists(UserSet.class);
            if (null == userSet) {
                userSet = new UserSet();
            } else {
                userSet.remove(newUser);
            }
            userSet.add(newUser);
            applicationStateManager.set(UserSet.class, userSet);
            @SuppressWarnings("unchecked")
            Class<USER> userClass = (Class<USER>) newUser.getClass();
            applicationStateManager.set(userClass, newUser);
            result = new LoginResult(newUser, credentials);
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
            for (IUser user : userSet) {
                handler.handleLogout(user);
                applicationStateManager.set(user.getClass(), null);
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
