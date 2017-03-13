package org.gagauz.tapestry.web.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gagauz.util.C;

public class Global {

    public static final String UUID_COOKIE_NAME = "uuid";

    private static final String EMPTY_UUID = "-1".intern();

    private static class RequestThreadData {
        final HttpServletRequest request;
        final HttpServletResponse response;
        String uuid = EMPTY_UUID;
        Map<Class<?>, Object> map = Collections.EMPTY_MAP;

        RequestThreadData(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }
    }

    private static final org.gagauz.util.Filter<Cookie> UUID_COOKIE = c -> {
        return c.getName().equals(UUID_COOKIE_NAME);
    };

    protected static Locale DEFAULT_LOCALE = Locale.ENGLISH;
    protected static ServletContext servletContext;
    private static final ThreadLocal<RequestThreadData> requestDataHolder = new ThreadLocal<>();

    public static ServletContext getServletContex() {
        return servletContext;
    }

    public static HttpServletRequest getRequest() {
        return requestDataHolder.get().request;
    }

    public static String getClientIp() {
        final String[] result = new String[] { "0" };
        Optional.ofNullable(requestDataHolder.get()).map(RequestThreadData::getRequest).ifPresent(r -> {
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
        return requestDataHolder.get().response;
    }

    public static Locale getLocale() {
        HttpServletRequest request = getRequest();
        return null == request || null == request.getLocale() ? DEFAULT_LOCALE : request.getLocale();
    }

    public static void init(HttpServletRequest request, HttpServletResponse response) {
        requestDataHolder.set(new RequestThreadData(request, response));
    }

    @SuppressWarnings("unchecked")
    public static <T> T peek(Class<T> class1) {
        return (T) requestDataHolder.get().map.get(class1);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> void put(Class<T> class1, T value) {
        Map map = requestDataHolder.get().map;
        if (Collections.EMPTY_MAP == map) {
            map = new HashMap<>();
            requestDataHolder.get().map = map;
        }
        map.put(class1, value);
    }

    public static void clear() {
        requestDataHolder.remove();
    }

    public static String getUuid() {
        final RequestThreadData requestThreadData = requestDataHolder.get();
        if (EMPTY_UUID.equals(requestThreadData.uuid)) {
            final Cookie cookie = C.find(Optional.ofNullable(getRequest().getCookies()).orElse(new Cookie[0]), UUID_COOKIE);
            if (null != cookie) {
                requestThreadData.uuid = cookie.getValue();
            } else {
                requestThreadData.uuid = UUID.randomUUID().toString();
                Cookie uuidCookie = new Cookie(UUID_COOKIE_NAME, requestThreadData.uuid);
                getResponse().addCookie(uuidCookie);
            }
        }
        return requestThreadData.uuid;
    }
}
