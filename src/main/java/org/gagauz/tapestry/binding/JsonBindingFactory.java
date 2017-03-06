package org.gagauz.tapestry.binding;

import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.BindingSource;

public class JsonBindingFactory implements BindingFactory {

    private final BindingSource bindingSource;
    private final TypeCoercer resolver;

    public JsonBindingFactory(BindingSource bindingSource, TypeCoercer resolver) {
        this.bindingSource = bindingSource;
        this.resolver = resolver;
    }

    @Override
    public Binding newBinding(final String description, final ComponentResources container, final ComponentResources component,
            String expression, final Location location) {

        return new AbstractContextBinding(bindingSource, resolver, description, container) {
            @Override
            public Object get() {
                return new JSONObject(expression);
            }
        };
    }
}
