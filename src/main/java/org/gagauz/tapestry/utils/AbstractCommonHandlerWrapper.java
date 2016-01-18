package org.gagauz.tapestry.utils;

import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.PageRenderRequestParameters;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractCommonHandlerWrapper.
 */
public abstract class AbstractCommonHandlerWrapper {

    /** The component event request parameters. */
    private final ComponentEventRequestParameters componentEventRequestParameters;

    /** The page render request parameters. */
    private final PageRenderRequestParameters pageRenderRequestParameters;

    /**
     * Instantiates a new abstract common handler wrapper.
     *
     * @param componentEventRequestParameters the component event request parameters
     */
    public AbstractCommonHandlerWrapper(ComponentEventRequestParameters componentEventRequestParameters) {
        this.componentEventRequestParameters = componentEventRequestParameters;
        this.pageRenderRequestParameters = null;
    }

    /**
     * Instantiates a new abstract common handler wrapper.
     *
     * @param pageRenderRequestParameters the page render request parameters
     */
    public AbstractCommonHandlerWrapper(PageRenderRequestParameters pageRenderRequestParameters) {
        this.componentEventRequestParameters = null;
        this.pageRenderRequestParameters = pageRenderRequestParameters;
    }

    /**
     * Handle.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public abstract void handle() throws IOException;

    /**
     * Gets the component event request parameters.
     *
     * @return the component event request parameters
     */
    public ComponentEventRequestParameters getComponentEventRequestParameters() {
        return componentEventRequestParameters;
    }

    /**
     * Gets the page render request parameters.
     *
     * @return the page render request parameters
     */
    public PageRenderRequestParameters getPageRenderRequestParameters() {
        return pageRenderRequestParameters;
    }

}
