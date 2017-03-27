package com.xl0e.tapestry.security.api;

import com.xl0e.tapestry.security.AccessDeniedException;
import com.xl0e.tapestry.util.AbstractCommonHandlerWrapper;

public interface AccessDeniedHandler {
    void handleException(AbstractCommonHandlerWrapper handlerWrapper, AccessDeniedException cause);
}
