package org.gagauz.tapestry.utils;

import org.apache.tapestry5.services.*;

import java.io.IOException;

/**
 * The Class AbstractCommonRequestFilter.
 */
public abstract class AbstractCommonRequestFilter implements PageRenderRequestFilter, ComponentEventRequestFilter {

    /**
     * Handle internal.
     *
     * @param handlerWrapper the handler wrapper
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void handleInternal(AbstractCommonHandlerWrapper handlerWrapper) throws IOException;

    @Override
    public void handle(final ComponentEventRequestParameters parameters, final ComponentEventRequestHandler handler) throws IOException {
        handleInternal(new AbstractCommonHandlerWrapper(parameters) {
            @Override
            public void handle() throws IOException {
                handler.handle(parameters);
            }
        });
    }

    @Override
    public void handle(final PageRenderRequestParameters parameters, final PageRenderRequestHandler handler) throws IOException {
        handleInternal(new AbstractCommonHandlerWrapper(parameters) {
            @Override
            public void handle() throws IOException {
                handler.handle(parameters);
            }
        });
    }
}
