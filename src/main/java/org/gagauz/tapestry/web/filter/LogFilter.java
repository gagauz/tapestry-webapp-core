package org.gagauz.tapestry.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gagauz.utils.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);
    private static final String SEPARATOR = "\n---------------------------------------\n";

    private static final String MDC_COOKIE_NAME = "uuid";
    private static final org.gagauz.utils.Filter<Cookie> COOKIE = c -> {
        return c.getName().equals(MDC_COOKIE_NAME);
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request0 = (HttpServletRequest) request;
        final HttpServletResponse response0 = (HttpServletResponse) response;
        Cookie cookie = C.find(Optional.ofNullable(request0.getCookies()).orElse(new Cookie[0]), COOKIE);
        String uid = null;
        if (null == cookie) {
            uid = UUID.randomUUID().toString();
            response0.addCookie(new Cookie(MDC_COOKIE_NAME, uid));
        } else {
            uid = cookie.getValue();
        }
        MDC.put(MDC_COOKIE_NAME, uid);
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Exception while processing request\n");
            sb.append(request0.getProtocol()).append(' ').append(request0.getMethod()).append(' ').append(request0.getRequestURI());
            if (null != request0.getQueryString()) {
                sb.append('?').append(request0.getQueryString());
            }
            sb.append(SEPARATOR);
            sb.append("Remote addr: ").append(request0.getRemoteAddr()).append('\n');
            sb.append("Server name: ").append(request0.getServerName());
            sb.append(SEPARATOR);
            Enumeration<String> names = request0.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                Enumeration<String> headers = request0.getHeaders(name);
                while (headers.hasMoreElements()) {
                    String header = headers.nextElement();
                    sb.append(name).append(':').append(' ').append(header).append('\n');
                }
            }
            sb.append(SEPARATOR);
            sb.append(request0.getParameterMap());
            sb.append(SEPARATOR);
            LOG.error(sb.toString(), e);
            throw e;
        } finally {
            MDC.remove(MDC_COOKIE_NAME);
        }
    }

    @Override
    public void destroy() {

    }

}
