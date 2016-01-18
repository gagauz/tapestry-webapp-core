package org.gagauz.tapestry.security.api;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.gagauz.hibernate.model.enums.AccessRole;
import org.gagauz.tapestry.security.AccessDeniedException;
import org.gagauz.tapestry.web.services.security.AnnotationAccessAttribute;

public class AccessAttributeCheckerImpl {

    @Inject
    private ApplicationStateManager applicationStateManager;

    @Inject
    private List<AccessAttributteChecker> checkers;

    public void check(AccessAttribute accessAttribute) throws AccessDeniedException {

        AccessRole[] needRoles = AccessRole.EMPTY_ROLES;

        if (accessAttribute instanceof AnnotationAccessAttribute) {
            needRoles = ((AnnotationAccessAttribute) accessAttribute).getRoles();
        }

        if (null != accessAttribute) {
            Repetitor repetitor = applicationStateManager.getIfExists(Repetitor.class);
            if (needRoles.length == 0 && null != repetitor) {
                return;
            }
            Manager manager = applicationStateManager.getIfExists(Manager.class);
            if (needRoles.length != 0 && null != manager) {
                for (AccessRole role : needRoles) {
                    if (manager.getRoles().contains(role)) {
                        return;
                    }
                }
            }
            throw new AccessDeniedException(accessAttribute);
        }
    }
}
