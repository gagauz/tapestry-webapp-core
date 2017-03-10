package org.gagauz.tapestry.security.api;

import org.gagauz.tapestry.security.AccessDeniedException;
import org.gagauz.tapestry.util.AbstractCommonHandlerWrapper;

public interface AccessDeniedHandler {
    void handleException(AbstractCommonHandlerWrapper handlerWrapper, AccessDeniedException cause);
}
