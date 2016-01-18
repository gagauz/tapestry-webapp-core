package org.gagauz.tapestry.web.services;

import org.gagauz.tapestry.security.api.Credentials;

public class LoginDetailsImpl implements Credentials {
	private final String username;
	private final String password;
	private final boolean remember;
	private final boolean admin;

	public LoginDetailsImpl(String username, String password, boolean remember, boolean admin) {
		this.username = username;
		this.password = password;
		this.remember = remember;
		this.admin = admin;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isRemember() {
		return remember;
	}

	public boolean isAdmin() {
		return admin;
	}
}
