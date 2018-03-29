package org.apache.tapestry5.security;

import java.lang.reflect.Modifier;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.MethodDescription;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.runtime.Event;
import org.apache.tapestry5.security.api.AccessAttributes;
import org.apache.tapestry5.security.api.AccessAttributesChecker;
import org.apache.tapestry5.security.api.AccessAttributesExtractor;
import org.apache.tapestry5.security.api.SessionAccessAttributes;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SecurityTransformer.
 */
public class SecurityTransformer implements ComponentClassTransformWorker2 {

    protected static Logger LOG = LoggerFactory.getLogger(SecurityTransformer.class);

    @Inject
    private AccessAttributesExtractor accessAttributeExtractor;

    @Inject
    private AccessAttributesChecker accessAttributeChecker;

    @Inject
    private SessionAccessAttributes sessionAccessAttributes;

    private void checkAccess(AccessAttributes attribute) throws AccessDeniedException {
        if (accessAttributeChecker.canAccess(sessionAccessAttributes.getSessionAttributes(), attribute)) {
            return;
        }
        throw new AccessDeniedException(attribute);
    }

    @Override
    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model) {
        final AccessAttributes attribute = accessAttributeExtractor.extract(plasticClass);

        if (null != attribute) {

            support.addEventHandler(EventConstants.ACTIVATE, 0,
                    "SecurityTransformer activate event handler", (instance, event) -> checkAccess(attribute));

            PlasticMethod setupRender = plasticClass.introduceMethod(new MethodDescription(Modifier.PUBLIC, "boolean", "setupRender",
                    new String[] { MarkupWriter.class.getName(), Event.class.getName() }, null, null));

            setupRender.addAdvice(invocation -> {
                try {
                    checkAccess(attribute);
                    invocation.proceed();
                } catch (AccessDeniedException ae) {
                    invocation.setReturnValue(false);
                    return;
                }
            });

            model.addRenderPhase(SetupRender.class);
        }
        for (PlasticMethod plasticMethod : plasticClass.getMethods()) {
            final AccessAttributes attribute1 = accessAttributeExtractor.extract(plasticClass, plasticMethod);

            if (null != attribute1) {
                plasticMethod.addAdvice(invocation -> {
                    checkAccess(attribute);
                    invocation.proceed();
                });
            }
        }
    }
}
