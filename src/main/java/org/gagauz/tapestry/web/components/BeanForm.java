package org.gagauz.tapestry.web.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BeanForm extends BeanEditForm {
	@Parameter
	@Property(write = false)
	private boolean showErrors;

	@Inject
	private ComponentResources resources;

	@Environmental
	private ValidationTracker tracker;

	public boolean isValid(final String fieldName) {
		return !tracker.inError(getField(fieldName));
	}

	private Field getField(String fieldName) {
		return new Field() {

			@Override
			public String getClientId() {
				return fieldName;
			}

			@Override
			public boolean isRequired() {
				return false;
			}

			@Override
			public boolean isDisabled() {
				return false;
			}

			@Override
			public String getLabel() {
				return null;
			}

			@Override
			public String getControlName() {
				return fieldName;
			}
		};
	}

	public void recordErrorCode(final String code) {
		String message = resources.getContainerMessages().get(code);
		recordError(message);
	}

	public void recordErrorCode(final String fieldName, final String code) {
		String message = resources.getContainerMessages().get(code);
		try {
			recordError(getField(fieldName), message);
		} catch (Exception e) {
		}
	}
}
