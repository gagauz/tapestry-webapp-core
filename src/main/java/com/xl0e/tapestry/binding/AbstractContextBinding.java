package com.xl0e.tapestry.binding;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.BindingSource;

public abstract class AbstractContextBinding extends AbstractBinding {
    final String description;
    final ComponentResources container;
    final BindingSource bindingSource;
    final TypeCoercer resolver;

    protected AbstractContextBinding(BindingSource bindingSource, TypeCoercer resolver, String description, ComponentResources container) {
        this.bindingSource = bindingSource;
        this.resolver = resolver;
        this.container = container;
        this.description = description;
    }

    protected <T> T getValue(String expr, String defaultPrefix, Class<T> clazz) {
        int d = expr.indexOf(':');
        if (d > 0 && d < expr.length()) {
            return resolver.coerce(
                    bindingSource.newBinding(description, container, expr.substring(0, d), expr.substring(d + 1)).get(),
                    clazz);
        }

        return (defaultPrefix.equals(BindingConstants.LITERAL)
                ? (T) expr
                : resolver.coerce(bindingSource.newBinding(description, container, defaultPrefix, expr).get(), clazz));

    }

    @Override
    public boolean isInvariant() {
        return false;
    }
}
