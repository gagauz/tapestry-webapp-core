package org.gagauz.tapestry.security;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.util.ExceptionUtils;
import org.gagauz.tapestry.security.api.AccessDeniedHandler;
import org.gagauz.tapestry.utils.AbstractCommonHandlerWrapper;
import org.gagauz.tapestry.utils.AbstractCommonRequestFilter;

import java.io.IOException;
import java.util.List;

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
        } catch (Throwable e) {
            AccessDeniedException cause = ExceptionUtils.findCause(e, AccessDeniedException.class);
            if (null != cause) {
                for (AccessDeniedHandler exceptionHandler : handlers) {
                    exceptionHandler.handleException(handlerWrapper, cause);
                }
                return;
            }
            // Pass non-security exception through.
            throw new RuntimeException(e);
        }
    }

}
