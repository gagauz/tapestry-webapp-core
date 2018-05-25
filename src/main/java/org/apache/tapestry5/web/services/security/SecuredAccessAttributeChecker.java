package org.apache.tapestry5.web.services.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.apache.tapestry5.security.api.AccessAttributesChecker;

public class SecuredAccessAttributeChecker implements AccessAttributesChecker<SecuredAccessAttributes> {

    @Override
    public boolean canAccess(final SecuredAccessAttributes sessionAttributes,
                             final SecuredAccessAttributes resourceAttributes) {
        final Collection<String> sessionAttributesList = Optional.ofNullable(sessionAttributes)
                .map(SecuredAccessAttributes::getAttributes)
                .orElse(Collections.emptyList());
        final Collection<String> resourceAttributesList = Optional.ofNullable(resourceAttributes)
                .map(SecuredAccessAttributes::getAttributes)
                .orElse(Collections.emptyList());

        return sessionAttributesList.stream().anyMatch(a -> resourceAttributesList.contains(a));
    }

}
