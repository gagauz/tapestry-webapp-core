package org.gagauz.tapestry.web.services.security;

import org.gagauz.hibernate.model.enums.AccessRole;

import org.gagauz.tapestry.security.api.AccessAttribute;

public class AnnotationAccessAttribute implements AccessAttribute {
	private final AccessRole[] roles;

	public AnnotationAccessAttribute(AccessRole... roles) {
		this.roles = roles;
	}

	public AccessRole[] getRoles() {
		return roles;
	}

}
