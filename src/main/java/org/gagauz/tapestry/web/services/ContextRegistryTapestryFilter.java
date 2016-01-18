package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.spring.TapestrySpringFilter;

import javax.servlet.ServletException;

/**
 * Adds the tapestry registry to the servlet context.
 * Special filter that adds in a T5 IoC module derived from the Spring WebApplicationContext.
 */
public class ContextRegistryTapestryFilter extends TapestrySpringFilter {
    private static final String REGISTRYNAME = "org.apache.tapestry.registry";

    @Override
    protected void init(Registry registry) throws ServletException {
        super.init(registry);
        getFilterConfig().getServletContext().setAttribute(REGISTRYNAME, registry);
    }

}
