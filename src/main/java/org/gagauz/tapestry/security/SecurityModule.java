package org.gagauz.tapestry.security;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.ComponentEventRequestFilter;
import org.apache.tapestry5.services.ComponentEventRequestHandler;
import org.apache.tapestry5.services.PageRenderRequestFilter;
import org.apache.tapestry5.services.PageRenderRequestHandler;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.gagauz.tapestry.security.api.AccessAttributeCheckerImpl;
import org.gagauz.tapestry.security.api.AccessAttributeExtractorImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class SecurityModule.
 */
public class SecurityModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(AccessDeniedExceptionInterceptorFilter.class).withId(
                "AccessDeniedExceptionInterceptorFilter");
        binder.bind(AuthenticationService.class).withId("AuthService");
        binder.bind(RememberMeHandler.class);
        binder.bind(RememberMeFilter.class);
        binder.bind(AccessAttributeExtractorImpl.class);
        binder.bind(AccessAttributeCheckerImpl.class);
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

}
