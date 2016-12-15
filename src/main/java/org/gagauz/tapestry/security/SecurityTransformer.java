package org.gagauz.tapestry.security;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.*;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.runtime.ComponentEvent;
import org.apache.tapestry5.runtime.Event;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentEventHandler;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.gagauz.tapestry.security.api.AccessAttribute;
import org.gagauz.tapestry.security.api.AccessAttributeExtractorChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

/**
 * The Class SecurityTransformer.
 */
public class SecurityTransformer implements ComponentClassTransformWorker2 {

    protected static Logger LOG = LoggerFactory.getLogger(SecurityTransformer.class);

    @Inject
    private AccessAttributeExtractorChecker accessAttributeExtractorChecker;

    @Inject
    private ApplicationStateManager applicationStateManager;

    private void checkAccess(AccessAttribute attribute) throws AccessDeniedException {
        PrincipalStorage userSet = applicationStateManager.getIfExists(PrincipalStorage.class);
        if (null != userSet) {
            if (accessAttributeExtractorChecker.check(userSet, attribute)) {
                return;
            }
        }
        throw new AccessDeniedException(attribute);
    }

    @Override
    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model) {
        final AccessAttribute attribute = accessAttributeExtractorChecker.extract(plasticClass, null);

        if (null != attribute) {

            support.addEventHandler(EventConstants.ACTIVATE, 0,
                    "SecurityTransformer activate event handler", new ComponentEventHandler() {
                        @Override
                        public void handleEvent(Component instance, ComponentEvent event) {
                            checkAccess(attribute);
                        }
                    });

            PlasticMethod setupRender = plasticClass.introduceMethod(new MethodDescription(Modifier.PUBLIC, "boolean", "setupRender",
                    new String[] {MarkupWriter.class.getName(), Event.class.getName()}, null, null));

            setupRender.addAdvice(new MethodAdvice() {

                @Override
                public void advise(MethodInvocation invocation) {
                    try {
                        checkAccess(attribute);
                        invocation.proceed();
                    } catch (AccessDeniedException ae) {
                        invocation.setReturnValue(false);
                        return;
                    }
                }
            });

            model.addRenderPhase(SetupRender.class);
        }
        for (PlasticMethod plasticMethod : plasticClass.getMethods()) {
            final AccessAttribute attribute1 = accessAttributeExtractorChecker.extract(plasticClass, plasticMethod);

            if (null != attribute1) {
                plasticMethod.addAdvice(new MethodAdvice() {
                    @Override
                    public void advise(MethodInvocation invocation) {
                        checkAccess(attribute);
                        invocation.proceed();
                    }
                });
            }
        }
    }
}
