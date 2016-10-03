package org.gagauz.tapestry.web.services.annotation;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.services.ComponentClassCache;
import org.apache.tapestry5.internal.transform.ReadOnlyComponentFieldConduit;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PerThreadValue;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.util.IdAllocator;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.FieldConduit;
import org.apache.tapestry5.plastic.InstanceContext;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.runtime.ComponentEvent;
import org.apache.tapestry5.services.ComponentEventHandler;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.URLEncoder;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.gagauz.tapestry.web.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SecurityTransformer.
 */
public class GetParamTransformer implements ComponentClassTransformWorker2 {

	/** The logger. */
	protected static Logger logger = LoggerFactory.getLogger(GetParamTransformer.class);

	/** The security checker. */
	@Inject
	private Request request;

	@Inject
	private ValueEncoderSource valueEncoderSource;

	@Inject
	private ComponentClassCache classCache;

	@Inject
	private URLEncoder urlEncoder;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private PerthreadManager perthreadManager;

	@Override
	public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model) {

		List<PlasticField> fields = plasticClass.getFieldsWithAnnotation(GetParam.class);
		for (PlasticField field : fields) {
			final GetParam fieldAnnotation = field.getAnnotation(GetParam.class);
			field.setConduit(createFieldValueConduitProvider(fieldAnnotation, field));
			final String parameterName = "".equals(fieldAnnotation.value()) ? field.getName() : fieldAnnotation.value();
			preallocateName(support, parameterName);
			field.claim(fieldAnnotation);
		}
	}

	private FieldConduit<Object> createFieldValueConduitProvider(final GetParam fieldAnnotation, PlasticField field) {
		final String fieldName = field.getName();
		final String fieldTypeName = field.getTypeName();

		Class<?> fieldType0 = this.classCache.forName(fieldTypeName);
		final String parameterName = "".equals(fieldAnnotation.value()) ? fieldName : fieldAnnotation.value();

		final boolean isArray = fieldType0.isArray();
		final Class<?> fieldType = isArray ? fieldType0.getComponentType() : fieldType0;

		return new ReadOnlyComponentFieldConduit(fieldName) {

			private PerThreadValue<Object> fieldValue;

			@Override
			public Object get(Object instance, InstanceContext context) {
				if (null == this.fieldValue) {
					this.fieldValue = GetParamTransformer.this.perthreadManager.createValue();
				} else if (this.fieldValue.exists()) {
					return this.fieldValue.get();
				}

				String[] clientValues = Global.getRequest().getParameterValues(parameterName);
				if (clientValues == null) {
					if (fieldType.isPrimitive()) {
						if (isArray) {
							Object array = Array.newInstance(fieldType, 1);
							Array.set(array, 0, 0);
							this.fieldValue.set(array);
						} else {
							this.fieldValue.set(0);
						}
					}
				} else {
					final ValueEncoder<?> encoder = GetParamTransformer.this.valueEncoderSource.getValueEncoder(fieldType);
					Object value = isArray ? Array.newInstance(fieldType, clientValues.length) : null;

					for (int i = 0; i < clientValues.length; i++) {
						clientValues[i] = GetParamTransformer.this.urlEncoder.decode(clientValues[i]);
						if (isArray) {
							Array.set(value, i, encoder.toValue(clientValues[i]));
						} else {
							value = encoder.toValue(clientValues[i]);
							break;
						}
					}

					this.fieldValue.set(value);

					// if (null != value &&
					// !fieldType.isAssignableFrom(value.getClass())) {
					// String message = String.format(
					// "Object %s (type %s) is not assignable to field %s (of
					// type %s).",
					// value, value.getClass(),
					// fieldName, fieldTypeName);
					//
					// throw new RuntimeException(message);
					// }
				}

				return this.fieldValue.get();
			}

			@Override
			public void set(Object instance, InstanceContext context, Object newValue) {
				this.fieldValue.set(newValue);
			}

		};

	}

	private static void preallocateName(TransformationSupport support, final String parameterName) {
		ComponentEventHandler handler = new ComponentEventHandler() {
			@Override
			public void handleEvent(Component instance, ComponentEvent event) {
				IdAllocator idAllocator = event.getEventContext().get(IdAllocator.class, 0);

				idAllocator.allocateId(parameterName);
			}
		};

		support.addEventHandler(EventConstants.PREALLOCATE_FORM_CONTROL_NAMES, 1,
				"ActivationRequestParameterWorker preallocate form control name '" + parameterName + "' event handler", handler);
	}
}
