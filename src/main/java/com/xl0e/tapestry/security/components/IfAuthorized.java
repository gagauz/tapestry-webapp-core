package com.xl0e.tapestry.security.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractConditional;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;

import com.xl0e.tapestry.security.AccessDeniedException;
import com.xl0e.tapestry.security.PrincipalStorage;
import com.xl0e.tapestry.security.api.AccessAttribute;
import com.xl0e.tapestry.security.api.AccessAttributeExtractorChecker;

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
