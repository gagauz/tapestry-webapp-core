package org.apache.tapestry5.web.config;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequestWrapper;

import com.xl0e.util.C;

public class Global {

    public static final String UUID_COOKIE_NAME = "uuid";
    public static final String UUID_ATTRIBUTE = "_uuid";

    private static final String EMPTY_UUID = "-1".intern();

    private static final com.xl0e.util.Filter<Cookie> UUID_COOKIE = c -> {
        return c.getName().equals(UUID_COOKIE_NAME);
    };

    protected static ServletContext servletContext;
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> responseHolder = new ThreadLocal<>();

    public static ServletContext getServletContex() {
        return servletContext;
    }

    public static HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    public static String getClientIp() {
        final String[] result = new String[] { "0" };
        Optional.ofNullable(getRequest()).ifPresent(r -> {
            result[0] = r.getHeader("X-Forwarded-For2");
            if (null == result[0]) {
                result[0] = r.getHeader("X-Forwarded-For");
                if (null == result[0]) {
                    result[0] = r.getRemoteAddr();
                }
            }
        });
        return result[0];
    }

    public static HttpServletResponse getResponse() {
        return responseHolder.get();
    }

    public static Locale getLocale() {
        HttpServletRequest request = getRequest();
        if (null == request.getLocale()) {
            wrapRequest(new HttpServletRequestWrapper(request) {
                @Override
                public Locale getLocale() {
                    return Locale.getDefault();
                }
            });
        }
        return request.getLocale();
    }

    private static void wrapRequest(HttpServletRequestWrapper httpServletRequestWrapper) {
        requestHolder.set(httpServletRequestWrapper);
    }

    public static void init(HttpServletRequest request, HttpServletResponse response) {
        requestHolder.set(request);
        responseHolder.set(response);
    }

    @SuppressWarnings("unchecked")
    public static <T> T peek(final Class<T> class1) {
        return (T) getRequest().getAttribute(class1.getName());
    }

    public static <T> void put(final Class<T> class1, final T value) {
        getRequest().setAttribute(class1.getName(), value);
    }

    public static void clear() {
        requestHolder.remove();
        responseHolder.remove();
    }

    public static String getUuid() {
        final HttpServletRequest request = getRequest();
        String uuid = (String) getRequest().getAttribute(UUID_ATTRIBUTE);
        if (null == uuid) {
            final Cookie cookie = C.find(Optional.ofNullable(getRequest().getCookies()).orElse(new Cookie[0]), UUID_COOKIE);
            if (null != cookie) {
                uuid = cookie.getValue();
                request.setAttribute(UUID_ATTRIBUTE, uuid);
            } else {
                uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
                Cookie uuidCookie = new Cookie(UUID_COOKIE_NAME, uuid);
                request.setAttribute(UUID_ATTRIBUTE, uuid);
                getResponse().addCookie(uuidCookie);
            }
        }
        return uuid;
    }
}
