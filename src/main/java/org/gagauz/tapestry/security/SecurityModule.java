package org.gagauz.tapestry.security;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.gagauz.tapestry.security.api.AccessDeniedHandler;
import org.gagauz.tapestry.security.api.AuthenticationHandler;
import org.gagauz.tapestry.security.api.User;
import org.gagauz.tapestry.utils.AbstractCommonHandlerWrapper;
import org.gagauz.tapestry.web.services.security.CookieEncryptorDecryptor;

import java.io.IOException;

/**
 * The Class SecurityModule.
 */
public class SecurityModule {

    private static final String REDIRECT_PAGE = "___rdpg__";

    public static void bind(ServiceBinder binder) {
        binder.bind(AccessDeniedExceptionInterceptorFilter.class).withId("AccessDeniedExceptionInterceptorFilter");
        binder.bind(AuthenticationService.class);
        binder.bind(RememberMeHandler.class);
        binder.bind(RememberMeFilter.class);
    }

    @Contribute(ComponentClassTransformWorker2.class)
    public void contributeComponentClassTransformWorker2(
                                                         OrderedConfiguration<ComponentClassTransformWorker2> configuration) {
        configuration.addInstance("SecurityTransformer", SecurityTransformer.class);
    }

    public void contributeComponentEventRequestHandler(
                                                       OrderedConfiguration<ComponentEventRequestFilter> configuration,
                                                       @Local AccessDeniedExceptionInterceptorFilter filter) {
        configuration.add("AccessDeniedExceptionInterceptorFilterComponent", filter, "after:*");
    }

    public void contributePageRenderRequestHandler(
                                                   OrderedConfiguration<PageRenderRequestFilter> configuration,
                                                   @Local AccessDeniedExceptionInterceptorFilter filter) {
        configuration.add("AccessDeniedExceptionInterceptorFilterPage", filter, "after:*");
    }

    @Contribute(ComponentEventRequestHandler.class)
    public static void contributeComponentEventRequestHandler(OrderedConfiguration<ComponentEventRequestFilter> configuration, RememberMeFilter handler) {
        configuration.add("ComponentEventRememberMeHandler", handler);
    }

    @Contribute(PageRenderRequestHandler.class)
    public static void contributePageRenderRequestHandler(OrderedConfiguration<PageRenderRequestFilter> configuration, RememberMeFilter handler) {
        configuration.add("PageRenderRememberMeHandler", handler);
    }

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
            public void handleLogin(LoginResult loginResult) {
                User user = loginResult.getUser();
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
