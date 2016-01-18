package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CookieService {

    private static final Pattern ZRD_LEVEL_DOMAIN_PREFIX = Pattern.compile("^[-_a-zA-Z0-9]+\\.([-_a-zA-Z0-9]+\\.[a-zA-Z0-9]+)$");

    @Inject
    private RequestGlobals requestGlobals;

    public Cookie addCookie(String key, String data, int maxAgeInSecond) {
        HttpServletResponse response = requestGlobals.getHTTPServletResponse();
        Cookie cookie = new Cookie(key, data);
        cookie.setMaxAge(maxAgeInSecond);
        cookie.setPath("/");
        response.addCookie(cookie);
        return cookie;
    }

    public void addCookie(String key, String data) {
        HttpServletResponse response = requestGlobals.getHTTPServletResponse();
        Cookie cookie = new Cookie(key, data);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        return null == cookie ? null : URLDecoder.decode(cookie.getValue());
    }

    public Cookie getCookie(String name) {

        HttpServletRequest request = requestGlobals.getHTTPServletRequest();

        if (request != null) {
            Cookie[] requestCookies = request.getCookies();
            if (requestCookies != null) {
                for (Cookie cookie : requestCookies) {
                    if (cookie.getName().equals(name)) {
                        return cookie;
                    }
                }
            }
        }

        return null;
    }

    public void deleteCookie(String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        requestGlobals.getHTTPServletResponse().addCookie(cookie);
    }

    public void addCookieWithSubDomain(String key, String value) {
        addCookieWithSubDomain(key, value, -1);
    }

    public void addCookieWithSubDomain(String key, String value, Number maxAge) {
        HttpServletResponse response = requestGlobals.getHTTPServletResponse();
        HttpServletRequest request = requestGlobals.getHTTPServletRequest();
        Cookie cookie = new Cookie(key, URLEncoder.encode(value));
        cookie.setMaxAge(maxAge.intValue());
        cookie.setPath(getCookiePath());
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }

    public void deleteCookieWithSubDomain(String key) {

        deleteCookie(key);

        Cookie cookie = new Cookie(key, null);
        cookie.setPath(getCookiePath());
        addHostForCookie(cookie);
        cookie.setMaxAge(0);

        requestGlobals.getHTTPServletResponse().addCookie(cookie);
    }

    private void addHostForCookie(Cookie cookie) {

        String host = requestGlobals.getHTTPServletRequest().getServerName();
        Matcher matcher = ZRD_LEVEL_DOMAIN_PREFIX.matcher(host);

        if (matcher.matches()) {
            cookie.setDomain('.' + matcher.group(1));
        }
    }

    private String getCookiePath() {

        String contextPath = requestGlobals.getHTTPServletRequest().getContextPath();

        return contextPath.length() > 0 ? contextPath : "/";
    }
}
