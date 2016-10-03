package org.gagauz.tapestry.web.config;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Global {
	protected static Locale DEFAULT_LOCALE = Locale.ENGLISH;
	protected static ServletContext servletContext;
	private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();
	private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();

	public static ServletContext getServletContex() {
		return servletContext;
	}

	public static HttpServletRequest getRequest() {
		return requestHolder.get();
	}

	public static HttpServletResponse getResponse() {
		return responseHolder.get();
	}

	public static Locale getLocale() {
		HttpServletRequest request = requestHolder.get();
		return null == request || null == request.getLocale() ? DEFAULT_LOCALE : request.getLocale();
	}

	public static void setRequest(HttpServletRequest request) {
		requestHolder.set(request);
	}

	public static void setResponse(HttpServletResponse response) {
		responseHolder.set(response);
	}

}
