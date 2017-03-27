package com.xl0e.tapestry.web.services;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.spring.TapestrySpringFilter;

/**
 * Adds the tapestry registry to the servlet context.
 * Special filter that adds in a T5 IoC module derived from the Spring WebApplicationContext.
 */
public class ContextRegistryTapestryFilter extends TapestrySpringFilter {
	private static final String REGISTRYNAME = "org.apache.tapestry.registry";
	public static final String APP_MODULE_CLASS = "tapestry.app-module-class";

	@Override
	protected void init(Registry registry) throws ServletException {
		super.init(registry);
		getFilterConfig().getServletContext().setAttribute(REGISTRYNAME, registry);
	}


	@Override
	protected Class<?>[] provideExtraModuleClasses(ServletContext context) {
		Class<?> appModuleClass = (Class<?>) context.getAttribute(APP_MODULE_CLASS);
		if (null == appModuleClass) {
			throw new IllegalStateException("No app module was provided!");
		}
		return new Class<?>[] { appModuleClass };
	}
}
