package org.gagauz.tapestry.security;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.gagauz.tapestry.security.api.AuthenticationHandler;
import org.gagauz.tapestry.security.api.LoginFormCredentials;
import org.gagauz.tapestry.security.api.User;
import org.gagauz.tapestry.web.services.security.CookieEncryptorDecryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RememberMeHandler implements AuthenticationHandler {

    private static final Logger log = LoggerFactory.getLogger(RememberMeHandler.class);

    static final String REMEMBER_ME_COOKIE_NAME = "rememberme";
    static final int REMEMBER_ME_COOKIE_AGE = 31536000;

    @Inject
    private Cookies cookies;

    @Inject
    private CookieEncryptorDecryptor cookieEncryptor;

    @Override
    public void handleLogout(User user) {
        cookies.removeCookieValue(REMEMBER_ME_COOKIE_NAME);
    }

    @Override
    public void handleLogin(LoginResult result) {
        log.info("Handle login!");

        if (result.isSuccess()
                && result.getCredentials() instanceof LoginFormCredentials) {
            LoginFormCredentials credentials = (LoginFormCredentials) result.getCredentials();
            String oldValue = cookies.readCookieValue(REMEMBER_ME_COOKIE_NAME);
            String hash = cookieEncryptor.encryptArray(credentials.getUsername(),
                    credentials.getPassword());
            if (null == oldValue || !oldValue.equals(hash)) {
                cookies.getBuilder(REMEMBER_ME_COOKIE_NAME, hash).setMaxAge(REMEMBER_ME_COOKIE_AGE).write();
            }
        }
    }

}
