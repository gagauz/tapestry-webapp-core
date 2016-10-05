package org.gagauz.tapestry.web.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);
	private static final String SEPARATOR = "---------------------------------------\n";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			HttpServletRequest request0 = (HttpServletRequest) request;
			StringBuilder sb = new StringBuilder("Exception while processing request:");
			sb.append(request0.getProtocol()).append(' ').append(request0.getMethod()).append(' ').append(request0.getPathInfo());
			if (null != request0.getQueryString()) {
				sb.append('?').append(request0.getQueryString()).append('\n');
			}
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
		}
	}

	@Override
	public void destroy() {

	}

}
