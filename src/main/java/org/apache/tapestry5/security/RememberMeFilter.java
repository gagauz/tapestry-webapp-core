package org.apache.tapestry5.security;

import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.security.api.User;
import org.apache.tapestry5.security.impl.CookieCredentials;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.web.services.security.CookieEncryptorDecryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xl0e.tapestry.util.AbstractCommonHandlerWrapper;
import com.xl0e.tapestry.util.AbstractCommonRequestFilter;

public class RememberMeFilter extends AbstractCommonRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RememberMeFilter.class);

    @Inject
    @Symbol(SecuritySymbols.REMEMBER_ME_COOKIE)
    private String cookieName;

    @Inject
    private Cookies cookies;

    @Inject
    private CookieEncryptorDecryptor cookieDecryptor;

    @Inject
    private AuthenticationService authService;

    @Inject
    private ApplicationStateManager applicationStateManager;

    @Override
    public void handleInternal(AbstractCommonHandlerWrapper handlerWrapper) throws IOException {
        PrincipalStorage users = applicationStateManager.getIfExists(PrincipalStorage.class);
        if (null == users || users.isEmpty()) {
            String cookieValue = cookies.readCookieValue(cookieName);
            if (null != cookieValue) {
                log.info("Handle remember me cookie [{}]", cookieValue);
                try {
                    User user = authService.login(new CookieCredentials(cookieValue));
                    if (null == user) {
                        throw new RuntimeException("remove cookie");
                    }
                } catch (Exception e) {
                    log.error("Failed to login with cookie. Remove it.", e);
                    cookies.removeCookieValue(cookieName);
                }
            }
        }
        handlerWrapper.handle();
    }

}
