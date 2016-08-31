package org.gagauz.utils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RootFilter implements javax.servlet.Filter {

    public static interface Handler extends FilterChain {

    }

    private static final ThreadLocal<HttpServletRequest> REQUEST = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> RESPONSE = new ThreadLocal<>();

    private static Handler BEFORE_CHAIN = new Handler() {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            REQUEST.set((HttpServletRequest) request);
            RESPONSE.set((HttpServletResponse) response);
        }
    };

    private static Handler AFTER_CHAIN = new Handler() {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            REQUEST.remove();
            RESPONSE.remove();
        }
    };

    public static HttpServletRequest getRequest() {
        return REQUEST.get();
    }

    public static HttpServletResponse getResponse() {
        return RESPONSE.get();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        BEFORE_CHAIN.doFilter(request, response);
        chain.doFilter(request, response);
        AFTER_CHAIN.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    public static void prependHandler(final Handler before) {
        BEFORE_CHAIN = appendFilterChain(AFTER_CHAIN, before);
    }

    public static void appendHandler(final Handler after) {
        AFTER_CHAIN = appendFilterChain(AFTER_CHAIN, after);
    }

    private static Handler appendFilterChain(final Handler original, final Handler wrapper) {
        return new Handler() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                original.doFilter(request, response);
                wrapper.doFilter(request, response);
            }
        };
    }

}
