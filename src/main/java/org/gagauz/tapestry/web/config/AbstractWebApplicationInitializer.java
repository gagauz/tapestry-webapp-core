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
import org.gagauz.tapestry.web.services.ContextRegistryTapestryFilter;
import org.gagauz.utils.RootFilter;
import org.gagauz.utils.StringUtils;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public abstract class AbstractWebApplicationInitializer implements WebApplicationInitializer {


	protected AbstractRefreshableWebApplicationContext rootContext;
	protected ServletContext servletContext;

	@Override
	public final void onStartup(ServletContext servletContext) throws ServletException {
		this.servletContext = servletContext;
		this.rootContext = createWebContext();
		this.rootContext.setConfigLocations(getConfigLocations());

		String appModule = getAppModuleClass().getName();
		Deque<String> deque = new ArrayDeque<>(Arrays.asList(appModule.split("\\.")));
		deque.removeLast();
		deque.removeLast();
		final String tapestryAppPackage = StringUtils.join(deque, '.');

		servletContext.setAttribute(ContextRegistryTapestryFilter.APP_MODULE_CLASS, getAppModuleClass());
		servletContext.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT,
				String.valueOf(getUseExternalSpringContext()));
		servletContext.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, tapestryAppPackage);
		servletContext.addListener(new ContextLoaderListener(this.rootContext));

		addFilter(StaticFilter.class, "/static/*");
		addFilter(RootFilter.class, "/*");

		FilterRegistration.Dynamic appFilter = addFilter(ContextRegistryTapestryFilter.class, "/*");
		appFilter.setInitParameter(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, tapestryAppPackage);
		appFilter.setInitParameter(SpringConstants.USE_EXTERNAL_SPRING_CONTEXT,
				String.valueOf(getUseExternalSpringContext()));
	}

	protected abstract Class<?> getAppModuleClass();

	protected abstract String[] getConfigLocations();

	protected abstract boolean getUseExternalSpringContext();

	protected final FilterRegistration.Dynamic addFilter(Class<? extends Filter> filterClass, String... urlMappings) {
		FilterRegistration.Dynamic filter = this.servletContext.addFilter(filterClass.getSimpleName(), filterClass);
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, urlMappings);
		return filter;
	}

	protected AbstractRefreshableWebApplicationContext createWebContext() {
		return new AnnotationConfigWebApplicationContext();
	}
}
