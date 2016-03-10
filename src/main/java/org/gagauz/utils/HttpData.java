package org.gagauz.utils;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpData implements javax.servlet.Filter {
    private static final ThreadLocal<HttpServletRequest> REQUEST = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> RESPONSE = new ThreadLocal<>();

    private static FilterChain BEFORE_CHAIN = new FilterChain() {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            REQUEST.set((HttpServletRequest) request);
            RESPONSE.set((HttpServletResponse) response);
        }
    };
    private static FilterChain AFTER_CHAIN = new FilterChain() {
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

    public static void addBeforeHandler(final FilterChain before) {
        final FilterChain original = BEFORE_CHAIN;
        BEFORE_CHAIN = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                original.doFilter(request, response);
                before.doFilter(request, response);
            }
        };
    }

    public static void addAfterHandler(final FilterChain after) {
        final FilterChain original = AFTER_CHAIN;
        AFTER_CHAIN = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                original.doFilter(request, response);
                after.doFilter(request, response);
            }
        };
    }

}
