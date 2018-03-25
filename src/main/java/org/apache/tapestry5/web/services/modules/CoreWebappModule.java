package org.apache.tapestry5.web.services.modules;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ComponentParameterConstants;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.Validator;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Ajax;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.BeanBlockOverrideSource;
import org.apache.tapestry5.services.BeanBlockSource;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.BindingSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.DisplayBlockContribution;
import org.apache.tapestry5.services.EditBlockContribution;
import org.apache.tapestry5.services.Html5Support;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Traditional;
import org.apache.tapestry5.services.URLEncoder;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.web.MySymbolConstants;
import org.apache.tapestry5.web.services.AlertManagerExt;
import org.apache.tapestry5.web.services.AlertManagerExtImpl;
import org.apache.tapestry5.web.services.CookieService;
import org.apache.tapestry5.web.services.CustomHttpResponse;
import org.apache.tapestry5.web.services.EmailRegexpAndHostValidator;
import org.apache.tapestry5.web.services.RedirectLink;
import org.apache.tapestry5.web.services.RequestMessagesPipeline;
import org.apache.tapestry5.web.services.ToolsService;
import org.apache.tapestry5.web.services.annotation.GetParamTransformer;
import org.apache.tapestry5.web.services.annotation.LongCacheTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xl0e.tapestry.binding.CondBindingFactory;
import com.xl0e.tapestry.binding.DateBindingFactory;
import com.xl0e.tapestry.binding.DeclineBindingFactory;
import com.xl0e.tapestry.binding.FormatBindingFactory;
import com.xl0e.tapestry.binding.MsgBindingFactory;
import com.xl0e.tapestry.binding.PageBindingFactory;
import com.xl0e.tapestry.validate.FileExtensionValidator;
import com.xl0e.tapestry.validate.NonLatinCharsValidator;

public class CoreWebappModule {

    private static final Logger LOG = LoggerFactory.getLogger(CoreWebappModule.class);

    public static void bind(ServiceBinder binder) {
        binder.bind(ToolsService.class);
        binder.bind(RequestMessagesPipeline.class);
        binder.bind(CookieService.class);
        binder.bind(AlertManagerExt.class, AlertManagerExtImpl.class);
    }

    @FactoryDefaults
    @Contribute(SymbolProvider.class)
    public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
        configuration.override(SymbolConstants.APPLICATION_CATALOG, "context:/WEB-INF/app.properties");
    }

    @ApplicationDefaults
    public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
        configuration.add(SymbolConstants.OMIT_GENERATOR_META, true);
        configuration.add(ComponentParameterConstants.GRID_TABLE_CSS_CLASS, "table table-responsive valign-center");
        configuration.add(SymbolConstants.FORM_GROUP_LABEL_CSS_CLASS, "col-sm-3 col-xs-12 control-label");
        configuration.add(SymbolConstants.FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_CSS_CLASS, "col-sm-9 col-xs-12");
        configuration.add(MySymbolConstants.FORM_CHECKBOX_LABEL_CSS_CLASS, "col-sm-offset-3 col-sm-9 col-xs-12");
        configuration.add(SymbolConstants.FORM_GROUP_WRAPPER_CSS_CLASS, "form-group");
        configuration.add(SymbolConstants.FORM_GROUP_FORM_FIELD_WRAPPER_ELEMENT_NAME, "div");

    }

    @Contribute(ComponentClassResolver.class)
    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
        configuration.add(new LibraryMapping(InternalConstants.CORE_LIBRARY, "org.apache.tapestry5.web"));
        configuration.add(new LibraryMapping(InternalConstants.CORE_LIBRARY, "org.apache.tapestry5.security"));
    }

    public static void contributeBindingSource(MappedConfiguration<String, BindingFactory> configuration,
                                               BindingSource bindingSource,
                                               TypeCoercer typeCoercer,
                                               Messages messages,
                                               ToolsService toolsService) {
        configuration.add("cond", new CondBindingFactory(bindingSource, typeCoercer));
        configuration.add("date", new DateBindingFactory(bindingSource, typeCoercer));
        configuration.add("msg", new MsgBindingFactory(bindingSource, typeCoercer, messages));
        configuration.add("format", new FormatBindingFactory(bindingSource, typeCoercer));
        configuration.add("page", new PageBindingFactory());
        configuration.add("decline", new DeclineBindingFactory(bindingSource, typeCoercer, toolsService));
    }

    public static void contributeFieldValidatorSource(
                                                      @SuppressWarnings("rawtypes") MappedConfiguration<String, Validator> configuration,
                                                      Messages messages,
                                                      JavaScriptSupport javaScriptSupport,
                                                      Html5Support html5Support) {
        configuration.add("emailhost", new EmailRegexpAndHostValidator(javaScriptSupport, html5Support));
        configuration.add("latin", new NonLatinCharsValidator(javaScriptSupport));
        configuration.add("extension", new FileExtensionValidator(javaScriptSupport));
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

    /**
     * Tapestry sends 302 redirect by default. Adding custom event result
     * processor we can send various response codes.
     */
    @Traditional
    @Contribute(ComponentEventResultProcessor.class)
    public static void contributeHttpStatusCodeEventResultProcessor(
                                                                    final MappedConfiguration<Class, ComponentEventResultProcessor> configuration,
                                                                    final Response response,
                                                                    final PageRenderLinkSource linkSource) {
        ComponentEventResultProcessor<CustomHttpResponse> processor = value -> {
            String pageUrl = "";
            if (null != value.getUrl()) {
                if (!value.getUrl().startsWith("/")) {
                    pageUrl = "/";
                }
                pageUrl += value.getUrl();
                response.setHeader("Location", pageUrl);
            }

            response.sendError(value.getCode(), value.getMessage());
        };

        ComponentEventResultProcessor<RedirectLink> redirectLinkProcessor = value -> {
            if (null != value.getPageClass()) {
                Link link = linkSource.createPageRenderLinkWithContext(value.getPageClass(), value.getContext());
                response.sendRedirect(link);
            } else {
                Link link = linkSource.createPageRenderLinkWithContext(value.getPageName(), value.getContext());
                response.sendRedirect(link);
            }
        };
        configuration.add(CustomHttpResponse.class, processor);
        configuration.add(RedirectLink.class, redirectLinkProcessor);
    }

    @Ajax
    @Contribute(ComponentEventResultProcessor.class)
    public static void contributeAjaxHttpStatusCodeEventResultProcessor(
                                                                        final MappedConfiguration<Class<CustomHttpResponse>, ComponentEventResultProcessor<CustomHttpResponse>> configuration,
                                                                        final Response response,
                                                                        @Symbol(SymbolConstants.CHARSET) final String outputEncoding) {
        configuration.add(CustomHttpResponse.class, value -> {
            JSONObject json = new JSONObject();
            if (null != value.getUrl()) {
                String pageUrl = "";
                if (!value.getUrl().startsWith("/")) {
                    pageUrl = "/";
                }
                pageUrl += value.getUrl();
                json.put("redirectURL", pageUrl);
            }
            ContentType contentType = new ContentType(InternalConstants.JSON_MIME_TYPE);
            PrintWriter writer = response.getPrintWriter(contentType.toString());

            json.print(writer);
            writer.flush();
            response.setStatus(value.getCode());
        });
    }

    public static void contributeDefaultDataTypeAnalyzer(@SuppressWarnings("rawtypes") MappedConfiguration<Class, String> configuration) {
        configuration.override(String.class, "string");
        configuration.override(Date.class, "date");
        configuration.add(BigDecimal.class, "bigDecimal");
        configuration.add(Float.class, "bigDecimal");
    }

    @Contribute(BeanBlockSource.class)
    public static void contributeBeanBlockSource(Configuration<BeanBlockContribution> configuration) {
        configuration.add(new EditBlockContribution("string", "AppPropertyBlocks", "string"));
        configuration.add(new EditBlockContribution("bigDecimal", "AppPropertyBlocks", "bigDecimal"));
    }

    @Contribute(BeanBlockOverrideSource.class)
    public static void contributeBeanBlockOverrideSource(Configuration<BeanBlockContribution> configuration) {
        configuration.add(new DisplayBlockContribution("date", "AppPropertyBlocks", "dateDisplay"));
        configuration.add(new DisplayBlockContribution("boolean", "AppPropertyBlocks", "booleanDisplay"));

        configuration.add(new EditBlockContribution("date", "AppPropertyBlocks", "date"));
        configuration.add(new EditBlockContribution("calendar", "AppPropertyBlocks", "calendar"));
        configuration.add(new EditBlockContribution("boolean", "AppPropertyBlocks", "boolean"));
    }

    /**
     * Contribute component class transform worker2.
     *
     * @param configuration
     *            the configuration
     */
    @Contribute(ComponentClassTransformWorker2.class)
    public void contributeComponentClassTransformWorker2(
                                                         OrderedConfiguration<ComponentClassTransformWorker2> configuration) {
        configuration.addInstance("GetParamTransformer", GetParamTransformer.class);
        configuration.addInstance("LongCacheTransformer", LongCacheTransformer.class);
    }

    // public RequestExceptionHandler buildAppRequestExceptionHandler(final ResponseRenderer renderer,
    // final ComponentSource componentSource,
    // final HttpServletRequest request,
    // final Response response) {
    // return exception -> {
    //
    // String exceptionPage = "Error500";
    //
    // LOG.error("Unhandled exception! Method = " + request.getMethod() + ", Url = " + request.getServletPath()
    // + " Referer = " + request.getHeader("Referer") + " User-Agent = " + request.getHeader("User-Agent")
    // + ", RemoteAddr = " + request.getRemoteAddr(), exception);
    //
    // response.setStatus(500);
    // try {
    // ExceptionReporter index = (ExceptionReporter) componentSource.getPage(exceptionPage);
    // LOG.info("reporting exception on " + index.getClass().getName());
    // index.reportException(exception);
    // } catch (Throwable ex) {
    // LOG.error("got error while reporting exception", ex);
    // }
    //
    // renderer.renderPageMarkupResponse(exceptionPage);
    // };
    // }

    // public void contributeServiceOverride(MappedConfiguration<Class, Object> configuration,
    // @Local RequestExceptionHandler handler) {
    // configuration.add(RequestExceptionHandler.class, handler);
    // }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Contribute(TypeCoercer.class)
    public static void contributeTypeCoercer(Configuration<CoercionTuple> configuration) {
        configuration.add(CoercionTuple.create(List.class, Set.class, input -> new HashSet(input)));

        configuration.add(CoercionTuple.create(Collection.class, EnumSet.class,
                input -> null == input || input.isEmpty() ? null : EnumSet.copyOf(input)));
    }
}
