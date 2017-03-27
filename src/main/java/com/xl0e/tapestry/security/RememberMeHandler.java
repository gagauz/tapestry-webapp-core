package com.xl0e.tapestry.security;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Cookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xl0e.tapestry.security.api.AuthenticationHandler;
import com.xl0e.tapestry.security.api.Principal;
import com.xl0e.tapestry.security.api.UserProvider;
import com.xl0e.tapestry.security.impl.CookieCredentials;

public class RememberMeHandler implements AuthenticationHandler {

    private static final Logger log = LoggerFactory.getLogger(RememberMeHandler.class);

    @Inject
    @Symbol(SecuritySymbols.REMEMBER_ME_COOKIE)
    private String cookieName;

    @Inject
    @Symbol(SecuritySymbols.REMEMBER_ME_COOKIE_AGE)
    private int cookieAge;

    @Inject
    private Cookies cookies;

    @Inject
    private UserProvider userProvider;

    @Override
    public void handleLogout(Principal user) {
        cookies.removeCookieValue(cookieName);
    }

    @Override
    public void handleLogin(LoginResult result) {
        log.info("Handle login!");

        if (result.isSuccess()
                && !(result.getCredentials() instanceof CookieCredentials)) {
            String oldValue = cookies.readCookieValue(cookieName);

            CookieCredentials cookie = userProvider.toCredentials(result.getUser(), CookieCredentials.class);

            if (null == oldValue || !oldValue.equals(cookie.getValue())) {
                cookies.getBuilder(cookieName, cookie.getValue()).setPath("/").setMaxAge(cookieAge).write();
            }
        }
    }

}
