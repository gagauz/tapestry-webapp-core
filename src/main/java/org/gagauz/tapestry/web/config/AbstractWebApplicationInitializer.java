package org.gagauz.tapestry.web.config;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.spring.SpringConstants;
import org.gagauz.tapestry.web.filter.LogFilter;
import org.gagauz.tapestry.web.filter.StaticInterceptorFilter;
import org.gagauz.tapestry.web.filter.UploadFilter;
import org.gagauz.tapestry.web.services.ContextRegistryTapestryFilter;
import org.gagauz.util.RequestInterceptorFilter;
import org.gagauz.util.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public abstract class AbstractWebApplicationInitializer implements WebApplicationInitializer {

    private static final String DEFAULT_STATIC_RESOURCE_PATH_MAPPING = "/static/*";
    private static final String DEFAULT_SERVLET_MAPPING = "/*";

    protected AnnotationConfigWebApplicationContext rootContext;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        Global.servletContext = servletContext;
        rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.setConfigLocations(getConfigLocations());

        String appModule = getAppModuleClass().getName();
        Deque<String> deque = new ArrayDeque<>(Arrays.asList(appModule.split("\\.")));
        deque.removeLast();
        deque.removeLast();
        final String tapestryAppPackage = StringUtils.join(deque, '.');

        servletContext.setAttribute(ContextRegistryTapestryFilter.APP_MODULE_CLASS, getAppModuleClass());
        servletContext.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, String.valueOf(getUseExternalSpringContext()));
        servletContext.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, tapestryAppPackage);
        servletContext.addListener(new ContextLoaderListener(rootContext));

        addFilter(StaticInterceptorFilter.class, getStaticResourcePathPrefix());
        addFilter(RequestInterceptorFilter.class, getServletMapping());
        addFilter(LogFilter.class, getServletMapping());
        addFilter(UploadFilter.class, getServletMapping());

        FilterRegistration.Dynamic appFilter = addFilter(ContextRegistryTapestryFilter.class, getServletMapping());
        appFilter.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, tapestryAppPackage);
        appFilter.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, String.valueOf(getUseExternalSpringContext()));
        MDC.put(Global.UUID_COOKIE_NAME, "system");
    }

    private String getStaticResourcePathPrefix() {
        return DEFAULT_STATIC_RESOURCE_PATH_MAPPING;
    }

    private String getServletMapping() {
        return DEFAULT_SERVLET_MAPPING;
    }

    protected abstract Class<?> getAppModuleClass();

    protected abstract String[] getConfigLocations();

    protected abstract boolean getUseExternalSpringContext();

    protected final FilterRegistration.Dynamic addFilter(Class<? extends Filter> filterClass, String... urlMappings) {
        final String filterName = filterClass.getSimpleName();
        final FilterRegistration.Dynamic filter = Global.servletContext.addFilter(filterName, filterClass);
        if (null == filter) {
            final FilterRegistration filterRegistration = Global.servletContext.getFilterRegistration(filterName);

            throw new IllegalStateException("Servlet context aready contains filter [" + filterName + "] of type "
                    + filterRegistration.getClassName() + ", mapped to " + filterRegistration.getUrlPatternMappings());
        }
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, urlMappings);
        return filter;
    }

}
