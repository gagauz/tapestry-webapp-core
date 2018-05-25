package org.apache.tapestry5.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.web.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestInterceptorFilter extends AbstractHttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptorFilter.class);

    public static interface Handler extends FilterChain {

    }

    private static Handler beforeChain = (request, response) -> {
        Global.init((HttpServletRequest) request, (HttpServletResponse) response);
    };

    private static Handler afterChain = (request, response) -> {
        Global.clear();
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        beforeChain.doFilter(request, response);
        final String requestUri = Global.getRequest().getRequestURI();
        chain.doFilter(request, response);
        afterChain.doFilter(request, response);
        LOG.info("Done filter chain for {} in {} ms", requestUri, (System.currentTimeMillis() - start));
    }

    @Override
    public void destroy() {
    }

    public static void prependHandler(final Handler before) {
        beforeChain = appendFilterChain(beforeChain, before);
    }

    public static void appendHandler(final Handler after) {
        afterChain = appendFilterChain(afterChain, after);
    }

    private static Handler appendFilterChain(final Handler original, final Handler wrapper) {
        return (request, response) -> {
            original.doFilter(request, response);
            wrapper.doFilter(request, response);
        };
    }

}
