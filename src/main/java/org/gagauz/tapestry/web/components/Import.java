package org.gagauz.tapestry.web.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeforeRenderBody;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.Initialization;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;

public class Import {

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String module;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String library;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String stack;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String stylesheet;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String init;

    @Parameter
    private Object[] parameters;

    @Inject
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private AssetSource assetSource;

    @BeforeRenderBody
    boolean renderBody() {
        return false;
    }

    void afterRender(MarkupWriter writer) {
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
                javaScriptSupport.importStylesheet(new StylesheetLink(assetSource.getExpandedAsset(stylesheet)));
            }
        }
        if (null != module) {
            Initialization initialization = javaScriptSupport.require(module);
            if (null != init) {
                initialization = initialization.invoke(init);
                if (null != parameters) {
                    initialization.with(parameters);
                }
            }
        }

    }
}
