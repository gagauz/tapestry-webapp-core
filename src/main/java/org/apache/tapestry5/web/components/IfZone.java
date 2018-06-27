package org.apache.tapestry5.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ClientBodyElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.PageLoaded;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.corelib.internal.ComponentActionSink;
import org.apache.tapestry5.corelib.internal.HiddenFieldPositioner;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ClientDataEncoder;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.Heartbeat;
import org.apache.tapestry5.services.HiddenFieldLocationRules;
import org.apache.tapestry5.services.compatibility.DeprecationWarning;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SupportsInformalParameters
@Import(module = "t5/core/zone")
public class IfZone implements ClientBodyElement {

    private final static Logger logger = LoggerFactory.getLogger(IfZone.class);

    @Parameter
    private boolean display;

    @Parameter(name = "else", defaultPrefix = BindingConstants.BLOCK)
    private Block elseBlock;

    /**
     * The element name to render for the zone; this defaults to the element actually used in the template, or "div" if
     * no specific element was specified.
     */
    @Parameter(required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
    private String elementName;

    /**
     * If bound, then the id attribute of the rendered element will be this exact value. If not bound, then a unique id
     * is generated for the element.
     */
    @Parameter(name = "id", defaultPrefix = BindingConstants.LITERAL)
    private String idParameter;

    @Environmental
    private JavaScriptSupport javascriptSupport;

    @Inject
    private Environment environment;

    /**
     * if set to true, then Ajax updates related to this Zone will, when rending, use simple IDs (not namespaced ids).
     * This is useful when the Zone contains a simple Form, as it (hopefully) ensures that the same ids used when
     * initially rendering, and when processing the submission, are also used when re-rendering the Form (to present
     * errors to the user). The default is false, maintaining the same behavior as in Tapestry 5.3 and earlier.
     *
     * @since 5.4
     */
    @Parameter(value = "true")
    private boolean simpleIds;

    @Inject
    private ComponentResources resources;

    @Inject
    private Heartbeat heartbeat;

    @Inject
    private ClientDataEncoder clientDataEncoder;

    @Inject
    private HiddenFieldLocationRules rules;

    private String clientId;

    private boolean insideForm;

    private HiddenFieldPositioner hiddenFieldPositioner;

    private ComponentActionSink actionSink;

    @Environmental(false)
    private FormSupport formSupport;

    @Inject
    private DeprecationWarning deprecationWarning;

    @Inject
    @Symbol(SymbolConstants.COMPACT_JSON)
    private boolean compactJSON;

    String defaultElementName() {
        return resources.getElementName("div");
    }

    boolean defaultDisplay() {
        return true;
    }

    @PageLoaded
    protected void pageLoaded() {
        deprecationWarning.ignoredComponentParameters(resources, "show", "update", "visible");
    }

    @BeginRender
    protected Object beginRender(MarkupWriter writer) {
        clientId = resources.isBound("id") ? idParameter : javascriptSupport.allocateClientId(resources);

        Element zoneElement = writer.element(elementName,
                "id", clientId,
                "data-container-type", "zone");

        if (simpleIds) {
            zoneElement.attribute("data-simple-ids", "true");
        }

        resources.renderInformalParameters(writer);

        return display ? getBody() : elseBlock;

        // insideForm = formSupport != null;
        //
        // if (insideForm) {
        // JSONObject parameters = new JSONObject(RequestConstants.FORM_CLIENTID_PARAMETER, formSupport.getClientId(),
        // RequestConstants.FORM_COMPONENTID_PARAMETER, formSupport.getFormComponentId());
        //
        // zoneElement.attribute("data-zone-parameters",
        // parameters.toString(compactJSON));
        //
        // hiddenFieldPositioner = new HiddenFieldPositioner(writer, rules);
        //
        // actionSink = new ComponentActionSink(logger, clientDataEncoder);
        //
        // environment.push(FormSupport.class, new FormSupportAdapter(formSupport) {
        // @Override
        // public <T> void store(T component, ComponentAction<T> action) {
        // actionSink.store(component, action);
        // }
        //
        // @Override
        // public <T> void storeCancel(T component, ComponentAction<T> action) {
        // actionSink.storeCancel(component, action);
        // }
        //
        // @Override
        // public <T> void storeAndExecute(T component, ComponentAction<T> action) {
        // store(component, action);
        //
        // action.execute(component);
        // }
        //
        // });
        // }

        // heartbeat.begin();
    }

    @AfterRender
    void afterRender(MarkupWriter writer) {
        // heartbeat.end();

        // if (insideForm) {
        // environment.pop(FormSupport.class);
        //
        // if (actionSink.isEmpty()) {
        // hiddenFieldPositioner.discard();
        // } else {
        //
        // Optional.ofNullable(hiddenFieldPositioner.getElement()).ifPresent(e -> {
        // e.attributes(
        // "type", "hidden",
        // "name", Form.FORM_DATA,
        // "value", actionSink.getClientData());
        // });
        // }
        // }

        writer.end(); // div
    }

    /**
     * The client id of the Zone; this is set when the Zone renders and will either be the value bound to the id
     * parameter, or an allocated unique id. When the id parameter is bound, this value is always accurate.
     * When the id parameter is not bound, the clientId is set during the {@linkplain BeginRender begin render phase}
     * and will be null or inaccurate before then.
     *
     * @return client-side element id
     */
    @Override
    public String getClientId() {
        if (resources.isBound("id"))
            return idParameter;

        // TAP4-2342. I know this won't work with a Zone with no given clientId and that was already
        // via AJAX inside an outer Zone, but it's still better than nothing.
        if (clientId == null) {
            clientId = resources.getId();
        }

        return clientId;
    }

    /**
     * Returns the zone's body (the content enclosed by its start and end tags). This is often used as part of an Ajax
     * partial page render to update the client with a fresh render of the content inside the zone.
     *
     * @return the zone's body as a Block
     */
    @Override
    public Block getBody() {
        return resources.getBody();
    }

}
