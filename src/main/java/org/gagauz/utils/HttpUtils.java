package org.gagauz.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class HttpUtils {
    public static String addRequestParam(String query, String name, String value) {
        if (StringUtils.isEmpty(query)) {
            if (null == value) {
                return "";
            }
            return '?' + name + '=' + URLEncoder.encode(value);
        }
        Map<String, String> map = parseQuery(query);
        if (StringUtils.isEmpty(value)) {
            map.remove(name);
        } else {
            map.put(name, URLEncoder.encode(value));
        }
        StringBuilder sb = new StringBuilder();
        if (!map.isEmpty()) {
            sb.append('?');
            for (Entry<String, String> e : map.entrySet()) {
                sb.append(e.getKey()).append('=').append(e.getValue()).append('&');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Map<String, String> parseQuery(String data) {
        Map<String, String> result = new HashMap<String, String>();
        if (null != data) {
            StringBuilder p = new StringBuilder();
            String lastKey = null;
            for (int i = 0; i < data.length(); i++) {
                char c = data.charAt(i);
                if (c == '=') {
                    lastKey = p.toString();
                    p = new StringBuilder();
                } else if (c == '&') {
                    result.put(lastKey, p.toString());
                    lastKey = null;
                    p = new StringBuilder();
                } else {
                    p.append(c);
                }
            }
            if (null != lastKey) {
                result.put(lastKey, p.toString());
            } else if (p.length() > 0) {
                result.put(p.toString(), "");
            }
        }
        return result;
    }
}
