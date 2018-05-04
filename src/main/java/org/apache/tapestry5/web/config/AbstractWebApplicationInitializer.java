package org.apache.tapestry5.web.config;

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
import org.apache.tapestry5.web.filter.LogFilter;
import org.apache.tapestry5.web.filter.RequestInterceptorFilter;
import org.apache.tapestry5.web.filter.StaticInterceptorFilter;
import org.apache.tapestry5.web.filter.UploadFilter;
import org.apache.tapestry5.web.services.ContextRegistryTapestryFilter;
import org.slf4j.MDC;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.xl0e.util.StringUtils;

public abstract class AbstractWebApplicationInitializer implements WebApplicationInitializer {

    private static final String DEFAULT_STATIC_RESOURCE_PATH_MAPPING = "/static/*";
    private static final String DEFAULT_SERVLET_MAPPING = "/*";

    protected ServletContext servletContext;
    protected AnnotationConfigWebApplicationContext rootContext;

    protected abstract Class<?> getAppModuleClass();

    protected abstract String[] getSpringConfigLocations();

    protected abstract boolean getUseExternalSpringContext();

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        Global.servletContext = servletContext;

        this.servletContext = servletContext;

        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        this.rootContext = springContext;

        springContext.setConfigLocations(getSpringConfigLocations());

        Global.applicationContext = springContext;

        servletContext.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, String.valueOf(getUseExternalSpringContext()));

        servletContext.addListener(new ContextLoaderListener(rootContext));

        createDefaultFilters();

        createTapestryAppFilter();

        MDC.put(Global.UUID_COOKIE_NAME, "system");
    }

    protected String getStaticResourcePathPrefix() {
        return DEFAULT_STATIC_RESOURCE_PATH_MAPPING;
    }

    protected String getServletMapping() {
        return DEFAULT_SERVLET_MAPPING;
    }

    protected void createDefaultFilters() {
        addFilter(StaticInterceptorFilter.class, getStaticResourcePathPrefix());
        addFilter(UploadFilter.class, getServletMapping());
        addFilter(RequestInterceptorFilter.class, getServletMapping());
        addFilter(LogFilter.class, getServletMapping());
    }

    protected void createTapestryAppFilter() {
        final String appModule = getAppModuleClass().getName();
        Deque<String> deque = new ArrayDeque<>(Arrays.asList(appModule.split("\\.")));
        deque.removeLast();
        deque.removeLast();
        final String tapestryAppPackage = StringUtils.join(deque, '.');

        servletContext.setAttribute(ContextRegistryTapestryFilter.APP_MODULE_CLASS, getAppModuleClass());
        servletContext.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, tapestryAppPackage);

        FilterRegistration.Dynamic appFilter = addFilter(ContextRegistryTapestryFilter.class, getServletMapping());
        appFilter.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, tapestryAppPackage);
        appFilter.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, String.valueOf(getUseExternalSpringContext()));
    }

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

    public AnnotationConfigWebApplicationContext getRootContext() {
        return rootContext;
    }
}
