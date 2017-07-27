package org.apache.tapestry5.security.api;

import org.apache.tapestry5.security.AccessDeniedException;

import com.xl0e.tapestry.util.AbstractCommonHandlerWrapper;

public interface AccessDeniedHandler {
    void handleException(AbstractCommonHandlerWrapper handlerWrapper, AccessDeniedException cause);
}
