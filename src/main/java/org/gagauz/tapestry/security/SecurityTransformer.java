package org.gagauz.tapestry.security;

import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.runtime.ComponentEvent;
import org.apache.tapestry5.services.ComponentEventHandler;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.gagauz.tapestry.security.api.AccessAttribute;
import org.gagauz.tapestry.security.api.AccessAttributeChecker;
import org.gagauz.tapestry.security.api.AccessAttributeExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class SecurityTransformer.
 */
public class SecurityTransformer implements ComponentClassTransformWorker2 {

    protected static Logger LOG = LoggerFactory.getLogger(SecurityTransformer.class);

    @Inject
    private AccessAttributeExtractor accessAttributeExtractor;

    @Inject
    private AccessAttributeChecker accessAttributesChecker;

    @Override
    public void transform(PlasticClass plasticClass, TransformationSupport support,
            MutableComponentModel model) {
        final AccessAttribute attribute = accessAttributeExtractor.extract(plasticClass);

        if (null != attribute) {
            support.addEventHandler(EventConstants.ACTIVATE, 0,
                    "SecurityTransformer activate event handler", new ComponentEventHandler() {
                        @Override
                        public void handleEvent(Component instance, ComponentEvent event) {
                            accessAttributesChecker.check(attribute);
                        }
                    });
        }
        for (PlasticMethod plasticMethod : plasticClass.getMethods()) {
            final AccessAttribute attribute1 = accessAttributeExtractor.extract(plasticClass,
                    plasticMethod);

            if (null != attribute1) {
                plasticMethod.addAdvice(new MethodAdvice() {
                    @Override
                    public void advise(MethodInvocation invocation) {
                        accessAttributesChecker.check(attribute1);
                        invocation.proceed();
                    }
                });
            }
        }
    }
}
