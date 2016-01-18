package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.spring.SpringConstants;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public abstract class AppWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, getUseExternalSpringContext().toString());
        servletContext.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, getTapestryAppPackage());

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(getConfigClasses());

        servletContext.addListener(new ContextLoaderListener(rootContext));

        FilterRegistration.Dynamic staticFilter = servletContext.addFilter("static", StaticFilter.class);
        staticFilter.addMappingForUrlPatterns(null, false, "/static/*");

        FilterRegistration.Dynamic appFilter = servletContext.addFilter("app", ContextRegistryTapestryFilter.class);
        appFilter.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, getTapestryAppPackage());
        appFilter.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT, getUseExternalSpringContext().toString());
        appFilter.addMappingForUrlPatterns(null, false, "/*");
    }

    protected abstract String getTapestryAppPackage();

    protected abstract Class<?>[] getConfigClasses();

    protected abstract Boolean getUseExternalSpringContext();
}
