package org.apache.tapestry5.web.services.security;

import org.apache.catalina.users.AbstractUser;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.security.PrincipalStorage;
import org.apache.tapestry5.security.api.AccessAttributeExtractorChecker;
import org.apache.tapestry5.security.api.Principal;

public class SecuredAccessAttributeExtractorChecker implements AccessAttributeExtractorChecker<SecuredAccessAttributes> {

    @Override
    public boolean check(PrincipalStorage userSet, SecuredAccessAttributes attribute) {
        if (null != userSet) {
            for (Principal user : userSet) {
                AbstractUser aUser = (AbstractUser) user;
                if (aUser.hasRole(attribute.getRoles())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public SecuredAccessAttributes extract(PlasticClass plasticClass, PlasticMethod plasticMethod) {

        if (null == plasticMethod) {
            Secured annotation = plasticClass.getAnnotation(Secured.class);
            if (null != annotation) {
                return new SecuredAccessAttributes(annotation.value());
            }
            return null;
        }

        Secured annotation = plasticMethod.getAnnotation(Secured.class);
        if (null != annotation) {
            return new SecuredAccessAttributes(annotation.value());
        }
        return null;
    }
}
