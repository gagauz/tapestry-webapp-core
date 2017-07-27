package com.xl0e.tapestry.binding;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.BindingSource;
import org.apache.tapestry5.web.services.ToolsService;

public class DeclineBindingFactory implements BindingFactory {

    private final BindingSource bindingSource;
    private final TypeCoercer resolver;
    private final ToolsService toolsService;

    public DeclineBindingFactory(BindingSource bindingSource, TypeCoercer resolver, ToolsService toolsService) {
        this.bindingSource = bindingSource;
        this.resolver = resolver;
        this.toolsService = toolsService;
    }

    @Override
    public Binding newBinding(final String description, final ComponentResources container, final ComponentResources component,
                              final String expression, final Location location) {

        final String[] parts = expression.split(",", 2);

        return new AbstractContextBinding(bindingSource, resolver, description, container) {
            @Override
            public Object get() {
                return toolsService.decline(parts[1], getValue(parts[0], BindingConstants.PROP, Integer.class));
            }
        };
    }

}
