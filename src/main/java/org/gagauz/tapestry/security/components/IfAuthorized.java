package org.gagauz.tapestry.security.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractConditional;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.gagauz.tapestry.security.AccessDeniedException;
import org.gagauz.tapestry.security.UserSet;
import org.gagauz.tapestry.security.api.AccessAttribute;
import org.gagauz.tapestry.security.api.AccessAttributeExtractorChecker;

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
            UserSet userSet = applicationStateManager.getIfExists(UserSet.class);
            accessAttributeChecker.check(userSet, attribute);
        } catch (AccessDeniedException e) {
            return false;
        }
        return true;
    }
}
