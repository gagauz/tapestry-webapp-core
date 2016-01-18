package org.gagauz.tapestry.web.services.security;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.gagauz.tapestry.security.*;
import org.gagauz.tapestry.security.api.AccessDeniedHandler;
import org.gagauz.tapestry.security.api.AuthenticationHandler;
import org.gagauz.tapestry.security.api.Credentials;
import org.gagauz.tapestry.security.api.User;
import org.gagauz.tapestry.utils.AbstractCommonHandlerWrapper;

import java.io.IOException;

@ImportModule(SecurityModule.class)
public class SecurityModuleSetup {

    private static final String REDIRECT_PAGE = "___rdpg__";

    public CookieEncryptorDecryptor buildSecurityEncryptor(
                                                           @Inject @Symbol(SymbolConstants.HMAC_PASSPHRASE) String passphrase) {
        return new CookieEncryptorDecryptor(passphrase, "salt");
    }

    @Contribute(AuthenticationService.class)
    public void contributeAuthenticationService(OrderedConfiguration<AuthenticationHandler> configuration,
                                                @Inject RememberMeHandler rememberMeHandler,
                                                @Inject final Response response, @Inject final Cookies cookies) {
        configuration.add("RememberMeLoginHandler", rememberMeHandler, "before:*");
        configuration.add("RedirectHandler", new AuthenticationHandler() {

            @Override
            public void handleLogin(User user, Credentials credentials) {

                if (null != user) {
                    String redirect = cookies.readCookieValue(REDIRECT_PAGE);
                    cookies.removeCookieValue(REDIRECT_PAGE);
                    if (null != redirect) {
                        try {
                            response.sendRedirect(redirect);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void handleLogout(User user) {
            }
        }, "after:*");
    }

    @Contribute(AccessDeniedExceptionInterceptorFilter.class)
    public static void contributeSecurityExceptionInterceptorFilter(
                                                                    OrderedConfiguration<AccessDeniedHandler> configuration,
                                                                    @Inject final Request request, @Inject final Response response,
                                                                    @Inject final Cookies cookies) {
        configuration.add("redirector", new AccessDeniedHandler() {
            @Override
            public void handleException(AbstractCommonHandlerWrapper handlerWrapper,
                                        AccessDeniedException cause) {
                String page = null;
                if (handlerWrapper.getComponentEventRequestParameters() != null) {
                    page = handlerWrapper.getComponentEventRequestParameters().getActivePageName();
                }
                if (handlerWrapper.getPageRenderRequestParameters() != null) {
                    page = handlerWrapper.getPageRenderRequestParameters().getLogicalPageName();
                }
                try {
                    cookies.getBuilder(REDIRECT_PAGE, request.getPath()).write();
                    if (page.toLowerCase().startsWith("admin")) {
                        response.sendRedirect("/admin/login");
                    } else {
                        response.sendRedirect("/login");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "after:*");
    }
}
