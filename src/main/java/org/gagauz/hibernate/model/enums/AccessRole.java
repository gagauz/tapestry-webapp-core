package org.gagauz.hibernate.model.enums;

public enum AccessRole {
	ADMIN,
	ORDER_MANAGER,
	USER_MANAGER,
	BLOG_MANAGER,
	SEO_MANAGER;

	public static final AccessRole[] EMPTY_ROLES = new AccessRole[0];
}
