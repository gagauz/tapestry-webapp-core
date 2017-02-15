package org.gagauz.tapestry.binding;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.BindingSource;

public class CondBindingFactory implements BindingFactory {

    private static final String DELIMITER = ",";
    private final BindingSource bindingSource;
    private final TypeCoercer resolver;

    public CondBindingFactory(BindingSource bindingSource, TypeCoercer resolver) {
        this.bindingSource = bindingSource;
        this.resolver = resolver;
    }

    @Override
    public Binding newBinding(final String description, final ComponentResources container, final ComponentResources component,
            String expression, final Location location) {
        final String[] parts = StringUtils.split(expression, DELIMITER, 3);

        if (parts.length < 3) {
            return bindingSource.newBinding(description, container, component, "prop", expression, location);
        }

        return new AbstractContextBinding(bindingSource, resolver, description, container) {
            @Override
            public Object get() {
                Boolean conditionValue = getValue(parts[0], BindingConstants.PROP, Boolean.class);
                if (conditionValue != null && conditionValue) {
                    return getValue(parts[1], BindingConstants.LITERAL, String.class);
                }
                return getValue(parts[2], BindingConstants.LITERAL, String.class);
            }

        };
    }

}
