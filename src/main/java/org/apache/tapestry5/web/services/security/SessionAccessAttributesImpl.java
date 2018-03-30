package org.apache.tapestry5.web.services.security;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.security.api.SessionAccessAttributes;
import org.apache.tapestry5.services.ApplicationStateManager;

public class SessionAccessAttributesImpl implements SessionAccessAttributes<SecuredAccessAttributes> {

    @Inject
    private ApplicationStateManager applicationStateManager;

    @Override
    public SecuredAccessAttributes getSessionAttributes() {
        return applicationStateManager.getIfExists(SecuredAccessAttributes.class);
    }

    @Override
    public void setSessionAttributes(SecuredAccessAttributes attributes) {
        applicationStateManager.set(SecuredAccessAttributes.class, attributes);
    }

}
