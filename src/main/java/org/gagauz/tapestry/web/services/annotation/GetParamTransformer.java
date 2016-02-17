package org.gagauz.tapestry.web.services.annotation;

import java.util.List;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.services.ComponentClassCache;
import org.apache.tapestry5.internal.transform.ReadOnlyComponentFieldConduit;
import org.apache.tapestry5.ioc.annotations.Inject;
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

    @Override
    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model) {

        List<PlasticField> fields = plasticClass.getFieldsWithAnnotation(GetParam.class);
        for (PlasticField field : fields) {
            final GetParam fieldAnnotation = field.getAnnotation(GetParam.class);
            field.setConduit(createFieldValueConduitProvider(fieldAnnotation, field));
            final String parameterName = "".equals(fieldAnnotation.value())
                    ? field.getName()
                    : fieldAnnotation.value();
            preallocateName(support, parameterName);
            field.claim(fieldAnnotation);
        }
    }

    private FieldConduit<Object> createFieldValueConduitProvider(final GetParam fieldAnnotation, PlasticField field) {
        final String fieldName = field.getName();
        final String fieldTypeName = field.getTypeName();

        return new ReadOnlyComponentFieldConduit(fieldName) {
            @Override
            public Object get(Object instance, InstanceContext context) {

                final Class<?> fieldType = classCache.forName(fieldTypeName);
                final String parameterName = "".equals(fieldAnnotation.value())
                        ? fieldName
                        : fieldAnnotation.value();

                String[] clientValues = requestGlobals.getHTTPServletRequest().getParameterValues(parameterName);
                if (clientValues == null) {
                    return null;
                }
                final ValueEncoder<?> encoder = valueEncoderSource.getValueEncoder(fieldType);
                Object value = null;

                for (int i = 0; i < clientValues.length; i++) {
                    clientValues[i] = urlEncoder.decode(clientValues[i]);
                    value = encoder.toValue(clientValues[i]);
                }

                if (!fieldType.isInstance(value)) {
                    String message = String.format(
                            "Object %s (type %s) is not assignable to field %s (of type %s).",
                            value, value.getClass().getName(),
                            fieldName, fieldTypeName);

                    throw new RuntimeException(message);
                }

                return value;
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
                "ActivationRequestParameterWorker preallocate form control name '" + parameterName
                        + "' event handler",
                handler);
    }
}
