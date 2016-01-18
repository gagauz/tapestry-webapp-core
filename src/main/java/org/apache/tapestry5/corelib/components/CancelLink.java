package org.apache.tapestry5.corelib.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractComponentEventLink;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CancelLink extends AbstractComponentEventLink {
    private static final String CANCEL = "Cancel";

    @Parameter
    private Object object;

    @Inject
    private ComponentResources resources;

    @Override
    protected Link createLink(Object[] eventContext) {
        return resources.createEventLink(CANCEL, eventContext);
    }

    public void onCancel() {
        object = null;
    }

}
