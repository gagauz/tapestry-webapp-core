package org.gagauz.tapestry.util;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.ValidationTracker;

public class FormHelper {

	public static void recordErrorCodeForField(ValidationTracker tracker, ComponentResources resources,
			String fieldName, String messageCode, Object... args) {
		if (args.length > 0) {
			tracker.recordError(getField(fieldName), resources.getMessages().format(messageCode, args));
			return;
		}
		tracker.recordError(getField(fieldName), resources.getMessages().get(messageCode));
	}

	public static void recordErrorCode(ValidationTracker tracker, ComponentResources resources, String messageCode,
			Object... args) {
		if (args.length > 0) {
			tracker.recordError(resources.getMessages().format(messageCode, args));
			return;
		}
		tracker.recordError(resources.getMessages().get(messageCode));
	}

	public static boolean isValid(ValidationTracker tracker, String fieldName) {
		return tracker.inError(getField(fieldName));
	}

	private static Field getField(final String fieldName) {
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

}
