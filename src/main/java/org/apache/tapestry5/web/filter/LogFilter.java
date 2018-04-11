package org.apache.tapestry5.web.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.web.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogFilter extends AbstractHttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);
    private static final String SEPARATOR = "\n---------------------------------------\n";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        MDC.put(Global.UUID_COOKIE_NAME, Global.getUuid());
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Exception while processing request\n");
            sb.append(request.getProtocol()).append(' ').append(request.getMethod()).append(' ').append(request.getRequestURI());
            if (null != request.getQueryString()) {
                sb.append('?').append(request.getQueryString());
            }
            sb.append(SEPARATOR);
            sb.append("Remote addr: ").append(request.getRemoteAddr()).append('\n');
            sb.append("Server name: ").append(request.getServerName());
            sb.append(SEPARATOR);
            Enumeration<String> names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                Enumeration<String> headers = request.getHeaders(name);
                while (headers.hasMoreElements()) {
                    String header = headers.nextElement();
                    sb.append(name).append(':').append(' ').append(header).append('\n');
                }
            }
            sb.append(SEPARATOR);
            sb.append(request.getParameterMap());
            sb.append(SEPARATOR);
            LOG.error(sb.toString(), e);
            throw e;
        } finally {
            MDC.remove(Global.UUID_COOKIE_NAME);
        }
    }

    @Override
    public void destroy() {

    }

}
