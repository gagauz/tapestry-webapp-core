package org.apache.tapestry5.web.mixins;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = "ajaxevent.js")
public class AjaxEvent {

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "click")
    private String bind;

    @Parameter
    private Object[] context;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, required = true)
    private String zone;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "change")
    private String event;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, value = "value")
    private String valueParam;

    @InjectContainer
    private ClientElement clientElement;

    @InjectContainer
    private Component container;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    protected Link createLink() {
        Link link = container.getComponentResources().createEventLink(event, context);
        return link;
    }

    void afterRender(MarkupWriter writer) {
        if (this.zone != null) {
            Link link = createLink();
            JSONObject spec = new JSONObject(
                    "elementId", clientElement.getClientId(),
                    "zoneId", zone,
                    "url", link.toURI(),
                    "valueParam", valueParam,
                    "bind", bind);
            javaScriptSupport.require("ajaxevent").invoke("init").with(spec);
        }
    }
}
