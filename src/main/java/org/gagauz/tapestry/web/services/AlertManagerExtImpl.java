package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.internal.alerts.AlertManagerImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

public class AlertManagerExtImpl extends AlertManagerImpl implements AlertManagerExt {

	private final Messages messages;

	public AlertManagerExtImpl(ApplicationStateManager asm, Request request, AjaxResponseRenderer ajaxResponseRenderer,
			PerthreadManager perThreadManager, Messages messages) {
		super(asm, request, ajaxResponseRenderer, perThreadManager);
		this.messages = messages;
	}

	@Override
	public void successCode(String messageCode, Object... args) {
		success(getMessage(messageCode, args));
	}

	@Override
	public void infoCode(String messageCode, Object... args) {
		info(getMessage(messageCode, args));
	}

	@Override
	public void warnCode(String messageCode, Object... args) {
		warn(getMessage(messageCode, args));
	}

	@Override
	public void errorCode(String messageCode, Object... args) {
		error(getMessage(messageCode, args));
	}

	protected String getMessage(String code, Object... args) {
		if (args.length > 0) {
			return messages.format(code, args);
		}
		return messages.get(code);
	}
}
