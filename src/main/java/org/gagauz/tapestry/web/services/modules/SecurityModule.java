package org.gagauz.tapestry.web.services.modules;

import java.io.IOException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.ComponentEventRequestFilter;
import org.apache.tapestry5.services.ComponentEventRequestHandler;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.PageRenderRequestFilter;
import org.apache.tapestry5.services.PageRenderRequestHandler;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.gagauz.tapestry.security.AccessDeniedExceptionInterceptorFilter;
import org.gagauz.tapestry.security.AuthenticationService;
import org.gagauz.tapestry.security.LoginResult;
import org.gagauz.tapestry.security.RememberMeFilter;
import org.gagauz.tapestry.security.RememberMeHandler;
import org.gagauz.tapestry.security.SecuritySymbols;
import org.gagauz.tapestry.security.SecurityTransformer;
import org.gagauz.tapestry.security.api.AccessDeniedHandler;
import org.gagauz.tapestry.security.api.AuthenticationHandler;
import org.gagauz.tapestry.security.api.Principal;
import org.gagauz.tapestry.security.impl.CookieCredentials;
import org.gagauz.tapestry.web.services.security.CookieEncryptorDecryptor;

/**
 * The Class SecurityModule.
 */
public class SecurityModule {

	public static final String ACCESS_DENIED_REDIRECTOR = "AccessDeniedRedirector";

	public static final String REDIRECT_PAGE_COOKIE_NAME = "___rdpg__";

	public static void bind(ServiceBinder binder) {
		binder.bind(AccessDeniedExceptionInterceptorFilter.class).withId("AccessDeniedExceptionInterceptorFilter");
		binder.bind(AuthenticationService.class);
		binder.bind(RememberMeHandler.class);
		binder.bind(RememberMeFilter.class);
	}

	@Contribute(SymbolProvider.class)
	@FactoryDefaults
	public static void setupDefaultSymbols(MappedConfiguration<String, Object> configuration) {
		configuration.add(SecuritySymbols.REMEMBER_ME_COOKIE, "_RMC_");
		configuration.add(SecuritySymbols.REMEMBER_ME_COOKIE_AGE, 365 * 24 * 3600);
	}

	@Contribute(ComponentClassTransformWorker2.class)
	public void contributeComponentClassTransformWorker2(
			OrderedConfiguration<ComponentClassTransformWorker2> configuration) {
		configuration.addInstance("SecurityTransformer", SecurityTransformer.class);
	}

	public void contributeComponentEventRequestHandler(OrderedConfiguration<ComponentEventRequestFilter> configuration,
			@Local AccessDeniedExceptionInterceptorFilter filter) {
		configuration.add("AccessDeniedExceptionInterceptorFilterComponent", filter, "after:*");
	}

	public void contributePageRenderRequestHandler(OrderedConfiguration<PageRenderRequestFilter> configuration,
			@Local AccessDeniedExceptionInterceptorFilter filter) {
		configuration.add("AccessDeniedExceptionInterceptorFilterPage", filter, "after:*");
	}

	@Contribute(ComponentEventRequestHandler.class)
	public static void contributeComponentEventRequestHandler(
			OrderedConfiguration<ComponentEventRequestFilter> configuration, RememberMeFilter handler) {
		configuration.add("ComponentEventRememberMeHandler", handler);
	}

	@Contribute(PageRenderRequestHandler.class)
	public static void contributePageRenderRequestHandler(OrderedConfiguration<PageRenderRequestFilter> configuration,
			RememberMeFilter handler) {
		configuration.add("PageRenderRememberMeHandler", handler);
	}

	public CookieEncryptorDecryptor buildSecurityEncryptor(
			@Inject @Symbol(SymbolConstants.HMAC_PASSPHRASE) String passphrase) {
		return new CookieEncryptorDecryptor(passphrase, "salt");
	}

	@Contribute(AuthenticationService.class)
	public void contributeAuthenticationService(OrderedConfiguration<AuthenticationHandler> configuration,
			@Inject RememberMeHandler rememberMeHandler, @Inject final Response response,
			@Inject final Cookies cookies) {
		configuration.add("RememberMeLoginHandler", rememberMeHandler, "before:*");
		configuration.add("RedirectHandler", new AuthenticationHandler() {

			@Override
			public void handleLogin(LoginResult loginResult) {
				Principal user = loginResult.getUser();
				if (null != user && !(loginResult.getCredentials() instanceof CookieCredentials)) {
					String redirect = cookies.readCookieValue(REDIRECT_PAGE_COOKIE_NAME);
					if (null != redirect) {
						cookies.removeCookieValue(REDIRECT_PAGE_COOKIE_NAME);
						try {
							response.sendRedirect(redirect);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void handleLogout(Principal user) {
			}
		}, "after:*");
	}

	@Contribute(AccessDeniedExceptionInterceptorFilter.class)
	public static void contributeSecurityExceptionInterceptorFilter(
			OrderedConfiguration<AccessDeniedHandler> configuration, @Inject final Request request,
			@Inject final Response response, @Inject final Cookies cookies) {
		configuration.add(ACCESS_DENIED_REDIRECTOR, (handlerWrapper, cause) -> {
			cause.printStackTrace();
			String page = null;
			if (handlerWrapper.getComponentEventRequestParameters() != null) {
				page = handlerWrapper.getComponentEventRequestParameters().getActivePageName();
			}
			if (handlerWrapper.getPageRenderRequestParameters() != null) {
				page = handlerWrapper.getPageRenderRequestParameters().getLogicalPageName();
			}
			try {
				cookies.getBuilder(REDIRECT_PAGE_COOKIE_NAME, request.getPath()).write();
				if (page.toLowerCase().startsWith("admin")) {
					response.sendRedirect("/admin/login");
				} else {
					response.sendRedirect("/login");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, "after:*");
	}

}
