package org.gagauz.tapestry.web.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.spring.SpringConstants;
import org.gagauz.tapestry.web.services.ContextRegistryTapestryFilter;
import org.gagauz.utils.HttpData;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public abstract class AppWebApplicationInitializer implements WebApplicationInitializer {

    protected AnnotationConfigWebApplicationContext rootContext;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, getUseExternalSpringContext().toString());
        servletContext.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, getTapestryAppPackage());

        rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(getConfigClasses());

        servletContext.addListener(new ContextLoaderListener(rootContext));

        FilterRegistration.Dynamic staticFilter = servletContext.addFilter("static", StaticFilter.class);
        staticFilter.addMappingForUrlPatterns(null, false, "/static/*");

        FilterRegistration.Dynamic httpDataFilter = servletContext.addFilter("httpdata", HttpData.class);
        httpDataFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST), false, "/*");

        FilterRegistration.Dynamic appFilter = servletContext.addFilter("app", ContextRegistryTapestryFilter.class);
        appFilter.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, getTapestryAppPackage());
        appFilter.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, getUseExternalSpringContext().toString());
        appFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST), false, "/*");
    }

    protected abstract String getTapestryAppPackage();

    protected abstract Class<?>[] getConfigClasses();

    protected abstract Boolean getUseExternalSpringContext();
}
