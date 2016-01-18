package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

public class JSSupport {

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @Inject
    private Request request;

    @Inject
    private Environment environment;

    public void addZone(final String zoneId, final Object zone) {
        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(zoneId, zone);
        }
    }

    public void addScript(final String script) {
        if (request.isXHR()) {
            ajaxResponseRenderer.addCallback(new JavaScriptCallback() {
                @Override
                public void run(JavaScriptSupport javascriptSupport) {
                    javascriptSupport.addScript(script);
                }
            });
        } else {
            /* 
            Иногда при многократных нажатиях на аякс ссылку происходит ошибка
             
            Caused by: org.apache.tapestry5.ioc.util.UnknownValueException: No object of type org.apache.tapestry5.services.javascript.JavaScriptSupport is available from the Environment.
                at org.apache.tapestry5.internal.services.EnvironmentImpl.peekRequired(EnvironmentImpl.java:88) ~[tapestry-core-5.3.6-patched.jar:na]
                
            */
            JavaScriptSupport javaScriptSupport = environment.peek(JavaScriptSupport.class);

            if (null != javaScriptSupport) {
                javaScriptSupport.addScript(script);
            } else {
                System.err
                        .println("Achtung!!! No object of type org.apache.tapestry5.services.javascript.JavaScriptSupport is available from the Environment.");
            }

        }
    }
}
