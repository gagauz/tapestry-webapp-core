package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.*;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.*;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.ImportModule;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.*;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.webresources.modules.WebResourcesModule;
import org.gagauz.tapestry.binding.*;
import org.gagauz.tapestry.hibernate.HibernateModule;
import org.gagauz.tapestry.security.SecurityModule;
import org.gagauz.tapestry.validate.NonLatinCharsValidator;
import org.gagauz.tapestry.web.services.annotation.GetParamTransformer;
import org.gagauz.tapestry.web.services.annotation.LongCacheTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@ImportModule({HibernateModule.class,
        TypeCoercerModule.class,
        SecurityModule.class,
        WebResourcesModule.class})
public class CoreWebappModule {

    private static final Logger LOG = LoggerFactory.getLogger(CoreWebappModule.class);

    public static void bind(ServiceBinder binder) {
        binder.bind(ToolsService.class);
        binder.bind(RequestMessagesPipeline.class);
        binder.bind(CookieService.class);
    }

    @ApplicationDefaults
    public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, "ru");
        configuration.add(SymbolConstants.MINIFICATION_ENABLED, true);
        configuration.add(SymbolConstants.ENABLE_HTML5_SUPPORT, true);
        //        configuration.add(SymbolConstants.ENABLE_PAGELOADING_MASK, "true");
        // configuration.add(SymbolConstants.DEFAULT_STYLESHEET,
        // null/*"org/repetitor/web/stack/app/default.css"*/);
        configuration.add(SymbolConstants.COMBINE_SCRIPTS, true);
        configuration.add(SymbolConstants.FORM_CLIENT_LOGIC_ENABLED, false);
        configuration.add(SymbolConstants.SECURE_ENABLED, true);
        configuration.add(SymbolConstants.JAVASCRIPT_INFRASTRUCTURE_PROVIDER, "jquery");
        configuration.add(SymbolConstants.FORM_FIELD_CSS_CLASS, "");
        configuration.add(SymbolConstants.EXCEPTION_REPORT_PAGE, "Error500");
    }

    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
        configuration
                .add(new LibraryMapping(InternalConstants.CORE_LIBRARY, "org.gagauz.tapestry"));
        configuration.add(new LibraryMapping(InternalConstants.CORE_LIBRARY,
                "org.gagauz.tapestry.security"));
    }

    public static void contributeBindingSource(MappedConfiguration<String, BindingFactory> configuration, BindingSource bindingSource, TypeCoercer typeCoercer, Messages messages, ToolsService toolsService) {
        configuration.add("cond", new CondBindingFactory(bindingSource, typeCoercer));
        configuration.add("date", new DateBindingFactory(bindingSource, typeCoercer));
        configuration.add("msg", new MsgBindingFactory(bindingSource, typeCoercer, messages));
        configuration.add("format", new FormatBindingFactory(bindingSource, typeCoercer));
        configuration.add("page", new PageBindingFactory());
        configuration.add("decline", new DeclineBindingFactory(bindingSource, typeCoercer,
                toolsService));
    }

    public static void contributeFieldValidatorSource(@SuppressWarnings("rawtypes") MappedConfiguration<String, Validator> configuration, Messages messages, JavaScriptSupport javaScriptSupport, Html5Support html5Support) {
        configuration.add("emailhost", new EmailRegexpAndHostValidator(javaScriptSupport, html5Support));
        configuration.add("latin", new NonLatinCharsValidator(javaScriptSupport));
    }

    @Contribute(ServiceOverride.class)
    public static void overrideUrlEncoder(MappedConfiguration<Class<?>, Object> configuration) {
        configuration.add(URLEncoder.class, new URLEncoder() {

            @Override
            public String decode(String input) {
                return input;
            }

            @Override
            public String encode(String input) {
                return input;
            }
        });
    }

    @Contribute(MarkupRenderer.class)
    public void contributeMarkupRenderer(final OrderedConfiguration<MarkupRendererFilter> configuration, final Environment environment) {

        MarkupRendererFilter validationDecorator = new MarkupRendererFilter() {
            @Override
            public void renderMarkup(final MarkupWriter markupWriter, MarkupRenderer renderer) {
                ValidationDecorator decorator = new AppValidationDecorator(markupWriter,
                        environment);
                environment.push(ValidationDecorator.class, decorator);
                renderer.renderMarkup(markupWriter);
                environment.pop(ValidationDecorator.class);
            }
        };

        configuration.override("ValidationDecorator", validationDecorator);
        //        configuration.override("InjectDefaultStylesheet", null);
    }

    @Contribute(PartialMarkupRenderer.class)
    public void contributePartialMarkupRenderer(final OrderedConfiguration<PartialMarkupRendererFilter> configuration, final Environment environment) {

        PartialMarkupRendererFilter validationDecorator = new PartialMarkupRendererFilter() {
            @Override
            public void renderMarkup(MarkupWriter markupWriter, JSONObject reply, PartialMarkupRenderer renderer) {
                ValidationDecorator decorator = new AppValidationDecorator(markupWriter,
                        environment);
                environment.push(ValidationDecorator.class, decorator);
                renderer.renderMarkup(markupWriter, reply);
                environment.pop(ValidationDecorator.class);
            }
        };

        configuration.override("ValidationDecorator", validationDecorator);
    }

    /**
     * Tapestry sends 302 redirect by default. Adding custom event result
     * processor we can send various response codes.
     */
    @Traditional
    @Contribute(ComponentEventResultProcessor.class)
    public static void contributeHttpStatusCodeEventResultProcessor(final MappedConfiguration<Class, ComponentEventResultProcessor> configuration, final Response response) {
        configuration.add(CustomHttpResponse.class,
                new ComponentEventResultProcessor<CustomHttpResponse>() {
                    @Override
                    public void processResultValue(CustomHttpResponse value) throws IOException {
                        String pageUrl = "";
                        if (null != value.getUrl()) {
                            if (!value.getUrl().startsWith("/")) {
                                pageUrl = "/";
                            }
                            pageUrl += value.getUrl();
                            response.setHeader("Location", pageUrl);
                        }

                        response.sendError(value.getCode(), value.getMessage());
                    }
                });
    }

    @Ajax
    @Contribute(ComponentEventResultProcessor.class)
    public static void contributeAjaxHttpStatusCodeEventResultProcessor(final MappedConfiguration<Class<CustomHttpResponse>, ComponentEventResultProcessor<CustomHttpResponse>> configuration, final Response response, @Symbol(SymbolConstants.CHARSET) final String outputEncoding) {
        configuration.add(CustomHttpResponse.class,
                new ComponentEventResultProcessor<CustomHttpResponse>() {
                    @Override
                    public void processResultValue(CustomHttpResponse value) throws IOException {
                        String pageUrl = "";
                        if (null != value.getUrl()) {
                            if (!value.getUrl().startsWith("/")) {
                                pageUrl = "/";
                            }
                            pageUrl += value.getUrl();
                        }
                        ContentType contentType = new ContentType(InternalConstants.JSON_MIME_TYPE);
                        PrintWriter writer = response.getPrintWriter(contentType.toString());
                        JSONObject json = new JSONObject();
                        json.put("redirectURL", pageUrl);
                        json.print(writer);
                        writer.flush();
                        response.setStatus(value.getCode());
                    }
                });
    }

    public static void contributeDefaultDataTypeAnalyzer(@SuppressWarnings("rawtypes") MappedConfiguration<Class, String> configuration) {
        configuration.override(String.class, "string");
    }

    public static void contributeBeanBlockSource(Configuration<BeanBlockContribution> configuration) {
        // configuration.add(new DisplayBlockContribution("string",
        // "AppPropertyBlocks", "string"));
        configuration.add(new EditBlockContribution("string", "AppPropertyBlocks", "string"));
    }

    /**
     * Contribute component class transform worker2.
     *
     * @param configuration
     *            the configuration
     */
    @Contribute(ComponentClassTransformWorker2.class)
    public void contributeComponentClassTransformWorker2(OrderedConfiguration<ComponentClassTransformWorker2> configuration) {
        configuration.addInstance("GetParamTransformer", GetParamTransformer.class);
        configuration.addInstance("LongCacheTransformer", LongCacheTransformer.class);
    }

    public RequestExceptionHandler buildAppRequestExceptionHandler(final ResponseRenderer renderer, final ComponentSource componentSource, final HttpServletRequest request, final Response response) {
        return new RequestExceptionHandler() {
            @Override
            public void handleRequestException(Throwable exception) throws IOException {

                String exceptionPage = "Error500";

                LOG.error(
                        "Unhandled exception! Method = " + request.getMethod() + ", Url = " + request.getServletPath() + " Referer = "
                                + request.getHeader("Referer") + " User-Agent = "
                                + request.getHeader("User-Agent") + ", RemoteAddr = " + request.getRemoteAddr(),
                        exception);

                response.setStatus(500);
                try {
                    ExceptionReporter index = (ExceptionReporter) componentSource.getPage(exceptionPage);
                    LOG.info("reporting exception on " + index.getClass().getName());
                    index.reportException(exception);
                } catch (Throwable ex) {
                    LOG.error("got error while reporting exception", ex);
                }

                renderer.renderPageMarkupResponse(exceptionPage);
            }
        };
    }

    public void contributeServiceOverride(MappedConfiguration<Class, Object> configuration, @Local RequestExceptionHandler handler) {
        configuration.add(RequestExceptionHandler.class, handler);
    }
}
