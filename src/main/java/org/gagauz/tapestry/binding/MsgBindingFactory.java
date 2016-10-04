package org.gagauz.tapestry.binding;

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
				Object value = getValue(expression, BindingConstants.LITERAL, Object.class);

				if (null != value && value.getClass().isEnum()) {
					value = value.getClass().getSimpleName() + "." + value;
				}
				String key = String.valueOf(value);
				if (MsgBindingFactory.this.messages.contains(key)) {
					return MsgBindingFactory.this.messages.get(key);
				}
				return key;
			}
		};
	}
}
