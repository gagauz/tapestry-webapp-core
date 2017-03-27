package com.xl0e.tapestry.binding;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.services.BindingFactory;

public class PageBindingFactory implements BindingFactory {

    public PageBindingFactory() {
    }

    @Override
    public Binding newBinding(final String description, final ComponentResources container, final ComponentResources component,
                              final String expression, final Location location) {

        final String pageName = container.getPageName();

        return new AbstractBinding(location) {
            @Override
            public Object get() {
                if (pageName.equalsIgnoreCase(expression)) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean isInvariant() {
                return false;
            }
        };
    }
}
