package org.gagauz.tapestry.web.mixins;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.MixinAfter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;

@MixinAfter
public class Import {

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String library;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String stack;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String stylesheet;

    /*
     * Tapestry initializers
     * 
     * T5.extendInitializers(function(options){
     * })
     * 
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String t5init;

    /*
     * RequireJS call
     * 
     * App.require(["App/widget/Tooltip"], function(F){
     *      F(options);
     * })
     *      
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String require;

    /*
     * Simple JS function call
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String init;

    /*
     * Simple JQuery function call
     */
    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String jqueryFunction;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private AssetSource assetSource;

    void beginRender(MarkupWriter writer) {
        String[] libraries;
        if (null != library) {
            libraries = StringUtils.split(library, ',');
            for (String lib : libraries) {
                if (lib.startsWith("/")) {
                    javaScriptSupport.importJavaScriptLibrary(lib.trim());
                } else {
                    javaScriptSupport.importJavaScriptLibrary(assetSource.getClasspathAsset(lib.trim()));
                }
            }
        }
        if (null != stack) {
            libraries = StringUtils.split(stack, ',');
            for (String stack : libraries) {
                javaScriptSupport.importStack(stack);
            }
        }
        if (null != stylesheet) {
            libraries = StringUtils.split(stylesheet, ',');
            for (String stylesheet : libraries) {
                javaScriptSupport.importStylesheet(new StylesheetLink(stylesheet));
            }
        }
    }

    void afterRender(MarkupWriter writer) {
        if (null != init || null != t5init || null != require || null != jqueryFunction) {
            JSONObject options = createOptions(writer);
            if (null != init) {
                javaScriptSupport.addScript(init, options.toCompactString());
            }
            if (null != t5init) {
                javaScriptSupport.addInitializerCall(t5init, options.toCompactString());
            }
            if (null != require) {
                String[] atoms = StringUtils.split(require, '/');
                String atom = atoms[atoms.length - 1];
                javaScriptSupport.addScript("App.require([\"%s\"], function(%s){new %s(%s)})", require, atom, atom, options.toCompactString());
            }
            if (null != jqueryFunction) {
                javaScriptSupport.addScript("jQuery('#%s').%s(%s)", options.remove("elementId"), jqueryFunction, options.toCompactString());
            }
        }
    }

    private JSONObject createOptions(MarkupWriter writer) {
        JSONObject options = new JSONObject();
        Element element = writer.getElement();

        if (null == element.getAttribute("id")) {
            String id = javaScriptSupport.allocateClientId(componentResources.getContainerResources());
            element.forceAttributes("id", id);
        }
        options.put("elementId", element.getAttribute("id"));
        return options;
    }
}
