package org.apache.tapestry5.web.services.security;

import java.util.Collection;
import java.util.Optional;

import org.apache.tapestry5.security.api.AccessAttributesChecker;

import com.xl0e.util.C;

public class SecuredAccessAttributeChecker implements AccessAttributesChecker<SecuredAccessAttributes> {

	@Override
	public boolean canAccess(final SecuredAccessAttributes sessionAttributes,
			final SecuredAccessAttributes resourceAttributes) {

		final Collection<String> sessionAttributesList = Optional.ofNullable(sessionAttributes)
				.map(SecuredAccessAttributes::getAttributes)
				.orElse(null);

		final Collection<String> resourceAttributesList = Optional.ofNullable(resourceAttributes)
				.map(SecuredAccessAttributes::getAttributes)
				.orElse(null);

		if (null == sessionAttributesList) {
			return false;
		}

		if (null != sessionAttributesList && C.isEmpty(resourceAttributesList)) {
			return true;
		}

		return sessionAttributesList.stream().anyMatch(a -> resourceAttributesList.contains(a));
	}

}
