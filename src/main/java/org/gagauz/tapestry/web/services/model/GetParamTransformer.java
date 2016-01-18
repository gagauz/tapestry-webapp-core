package org.gagauz.tapestry.web.services.model;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.internal.services.ComponentClassCache;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.util.IdAllocator;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.FieldHandle;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticField;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.runtime.ComponentEvent;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

            final String parameterName = "".equals(fieldAnnotation.value())
                    ? field.getName()
                    : fieldAnnotation.value();

            // Assumption: the field type is not one that's loaded by the
            // component class loader, so it's safe
            // to convert to a hard type during class transformation.

            Class<?> fieldType = classCache.forName(field.getTypeName());

            final ValueEncoder<?> encoder = valueEncoderSource.getValueEncoder(fieldType);

            final FieldHandle handle = field.getHandle();

            String fieldName = String.format("%s.%s", field.getPlasticClass().getClassName(),
                    field.getName());

            ComponentEventHandler eventHandler = new ComponentEventHandler() {
                @Override
                public void handleEvent(Component instance, ComponentEvent event) {

                    String clientValue = requestGlobals.getHTTPServletRequest().getParameter(parameterName);

                    if (clientValue == null) {
                        return;
                    }

                    clientValue = urlEncoder.decode(clientValue);
                    Object value = encoder.toValue(clientValue);
                    handle.set(instance, value);
                }
            };

            support.addEventHandler(EventConstants.ACTIVATE, 0, String.format(
                    "Restoring field %s from query parameter '%s'", fieldName, parameterName),
                    eventHandler);

            support.addEventHandler(EventConstants.SUBMIT, 0, String.format(
                    "Restoring field %s from query parameter '%s'", fieldName, parameterName),
                    eventHandler);

            support.addEventHandler(EventConstants.ACTION, 0, String.format(
                    "Restoring field %s from query parameter '%s'", fieldName, parameterName),
                    eventHandler);

            // decorateLinks(support, fieldName, handle, parameterName, encoder,
            // urlEncoder);

            preallocateName(support, parameterName);
        }
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
