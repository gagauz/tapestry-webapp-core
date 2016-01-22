package org.gagauz.tapestry.security;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Cookies;
import org.gagauz.tapestry.security.api.User;
import org.gagauz.tapestry.security.impl.CookieCredentials;
import org.gagauz.tapestry.utils.AbstractCommonHandlerWrapper;
import org.gagauz.tapestry.utils.AbstractCommonRequestFilter;
import org.gagauz.tapestry.web.services.security.CookieEncryptorDecryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
        User user = applicationStateManager.getIfExists(User.class);
        if (null == user) {
            String cookieValue = cookies.readCookieValue(cookieName);
            if (null != cookieValue) {
                log.info("Handle remember me cookie [{}]", cookieValue);
                try {
                    user = authService.login(new CookieCredentials(cookieValue));
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
