package com.xl0e.tapestry.binding;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.BindingSource;

public class FormatBindingFactory implements BindingFactory {

    private final BindingSource bindingSource;
    private final TypeCoercer resolver;

    public FormatBindingFactory(BindingSource bindingSource, TypeCoercer resolver) {
        this.bindingSource = bindingSource;
        this.resolver = resolver;
    }

    @Override
    public Binding newBinding(final String description,
                              final ComponentResources container,
                              final ComponentResources component,
                              final String expression,
                              final Location location) {

        final String[] parts = expression.split(",", 2);

        return new AbstractContextBinding(bindingSource, resolver, description, container) {
            @Override
            public Object get() {
                if (parts[0].contains("f"))
                    return String.format(parts[0], getValue(parts[1], BindingConstants.PROP, Double.class));
                if (parts[0].contains("d"))
                    return String.format(parts[0], getValue(parts[1], BindingConstants.PROP, Integer.class));
                return String.format(parts[0], getValue(parts[1], BindingConstants.PROP, String.class));
            }
        };
    }
}
