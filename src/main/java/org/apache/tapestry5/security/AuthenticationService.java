package org.apache.tapestry5.security;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.xl0e.util.C;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.security.api.AccessAttributes;
import org.apache.tapestry5.security.api.AuthenticationHandler;
import org.apache.tapestry5.security.api.Credentials;
import org.apache.tapestry5.security.api.SessionAccessAttributes;
import org.apache.tapestry5.security.api.User;
import org.apache.tapestry5.security.api.UserProvider;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    @Inject
    private UserProvider userProvider;

    @Inject
    private SessionAccessAttributes sessionAccessAttributes;

    @Inject
    private List<AuthenticationHandler> handlers;

    @Inject
    private Request request;

    public User login(Credentials credential) {
        LoginResult result = null;
        User newUser = userProvider.findByCredential(credential);
        if (null != newUser) {
            copySession();
            sessionAccessAttributes.setSessionAttributes(newUser.getAccessAttributes());
            result = new LoginResult(newUser, credential);
        } else {
            result = new LoginResult(credential);
        }
        for (AuthenticationHandler handler : handlers) {
            handler.handleLogin(result);
        }

        return newUser;
    }

    public <P extends User> P login(P newUser) {
        return null;
    }

    public void logout() {

        AccessAttributes user = sessionAccessAttributes.getSessionAttributes();
        sessionAccessAttributes.setSessionAttributes(null);

        for (AuthenticationHandler handler : handlers) {
            handler.handleLogout(user);
        }

        Session session = request.getSession(false);

        if (null != session) {
            try {
                session.invalidate();
            } catch (Exception e) {
                log.error("Session invalidate error", e);
            }
        }
    }

    private void copySession() {
        Session old = request.getSession(false);
        Map<String, Object> attr = C.hashMap();
        Optional.ofNullable(old).map(Session::getAttributeNames).orElse(Collections.emptyList()).forEach(n -> {
            attr.put(n, old.getAttribute(n));
        });
        Session newSession = request.getSession(true);
        attr.forEach((k, v) -> newSession.setAttribute(k, v));

    }

}
