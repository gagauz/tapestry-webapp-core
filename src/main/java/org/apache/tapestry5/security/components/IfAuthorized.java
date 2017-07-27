package org.apache.tapestry5.security.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractConditional;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.security.AccessDeniedException;
import org.apache.tapestry5.security.PrincipalStorage;
import org.apache.tapestry5.security.api.AccessAttribute;
import org.apache.tapestry5.security.api.AccessAttributeExtractorChecker;
import org.apache.tapestry5.services.ApplicationStateManager;

public class IfAuthorized extends AbstractConditional {

    /** The roles. */
    @Parameter
    private AccessAttribute attribute;

    @Inject
    private ApplicationStateManager applicationStateManager;

    @Inject
    private AccessAttributeExtractorChecker accessAttributeChecker;

    @Override
    protected boolean test() {
        try {
            PrincipalStorage userSet = applicationStateManager.getIfExists(PrincipalStorage.class);
            accessAttributeChecker.check(userSet, attribute);
        } catch (AccessDeniedException e) {
            return false;
        }
        return true;
    }

}
