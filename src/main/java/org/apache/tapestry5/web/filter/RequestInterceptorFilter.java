package org.apache.tapestry5.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.web.config.Global;

public class RequestInterceptorFilter implements javax.servlet.Filter {

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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		beforeChain.doFilter(request, response);
		chain.doFilter(request, response);
		afterChain.doFilter(request, response);
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
