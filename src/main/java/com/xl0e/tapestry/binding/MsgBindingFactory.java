package com.xl0e.tapestry.binding;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.BindingSource;

public class MsgBindingFactory implements BindingFactory {

    private final BindingSource bindingSource;
    private final TypeCoercer resolver;
    private final Messages messages;

    public MsgBindingFactory(BindingSource bindingSource, TypeCoercer resolver, Messages messages) {
        this.bindingSource = bindingSource;
        this.resolver = resolver;
        this.messages = messages;
    }

    @Override
    public Binding newBinding(final String description, final ComponentResources container, final ComponentResources component,
            final String expression, final Location location) {

        return new AbstractContextBinding(this.bindingSource, this.resolver, description, container) {
            @Override
            public Object get() {

                String[] expressions = StringUtils.split(expression, ',');
                Object[] args = new Object[expressions.length - 1];

                Object value = getValue(expressions[0], BindingConstants.LITERAL, Object.class);
                for (int i = 1; i < expressions.length; i++) {
                    args[i - 1] = getValue(expressions[i], BindingConstants.LITERAL, Object.class);
                }

                String key = String.valueOf(value);
                if (args.length > 0) {
                    return MsgBindingFactory.this.messages.format(key, args);
                }
                if (null != value && value.getClass().isEnum()) {
                    value = value.getClass().getSimpleName() + "." + String.valueOf(value);
                }
                return MsgBindingFactory.this.messages.get(key);
            }
        };
    }
}
