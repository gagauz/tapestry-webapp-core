package com.xl0e.tapestry.security;

import java.io.IOException;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.util.ExceptionUtils;

import com.xl0e.tapestry.security.api.AccessDeniedHandler;
import com.xl0e.tapestry.util.AbstractCommonHandlerWrapper;
import com.xl0e.tapestry.util.AbstractCommonRequestFilter;

public class AccessDeniedExceptionInterceptorFilter extends AbstractCommonRequestFilter {

	@Inject
	private List<AccessDeniedHandler> handlers;

	@Override
	public void handleInternal(AbstractCommonHandlerWrapper handlerWrapper) throws IOException {
		try {
			handlerWrapper.handle();
		} catch (IOException io) {
			// Pass IOException through.
			throw io;
		} catch (Exception e) {
			AccessDeniedException cause = ExceptionUtils.findCause(e, AccessDeniedException.class);
			if (null != cause) {
				for (AccessDeniedHandler exceptionHandler : this.handlers) {
					exceptionHandler.handleException(handlerWrapper, cause);
				}
				return;
			}
			// Pass non-security exception through.
			throw e;
		}
	}

}
