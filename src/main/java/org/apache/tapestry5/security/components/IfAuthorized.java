package org.apache.tapestry5.security.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.base.AbstractConditional;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.security.AccessDeniedException;
import org.apache.tapestry5.security.api.AccessAttributes;
import org.apache.tapestry5.security.api.AccessAttributesChecker;
import org.apache.tapestry5.security.api.SessionAccessAttributes;

public class IfAuthorized extends AbstractConditional {

    /** The roles. */
    @Parameter
    private AccessAttributes attribute;

    @Inject
    private SessionAccessAttributes sessionAttributes;

    @Inject
    private AccessAttributesChecker accessAttributeChecker;

    @Override
    protected boolean test() {
        try {
            accessAttributeChecker.canAccess(sessionAttributes.getSessionAttributes(), attribute);
        } catch (AccessDeniedException e) {
            return false;
        }
        return true;
    }

}
