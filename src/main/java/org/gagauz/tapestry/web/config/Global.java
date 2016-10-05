package org.gagauz.tapestry.web.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Global {
	protected static Locale DEFAULT_LOCALE = Locale.ENGLISH;
	protected static ServletContext servletContext;
	private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();
	private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();
	private static final ThreadLocal<Map<Class<?>, Object>> requestDataHolder = new ThreadLocal<>();

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

	@SuppressWarnings("unchecked")
	public static void init(HttpServletRequest request, HttpServletResponse response) {
		requestHolder.set(request);
		responseHolder.set(response);
		requestDataHolder.set(Collections.EMPTY_MAP);
	}

	@SuppressWarnings("unchecked")
	public static <T> T peek(Class<T> class1) {
		return (T) requestDataHolder.get().get(class1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> void put(Class<T> class1, T value) {
		Map map = requestDataHolder.get();
		if (Collections.EMPTY_MAP == map) {
			map = new HashMap<>();
			requestDataHolder.set(map);
		}
		map.put(class1, value);
	}

	public static void clear() {
		requestDataHolder.remove();
		responseHolder.remove();
		requestHolder.remove();
	}

}
