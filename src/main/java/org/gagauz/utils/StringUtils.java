package org.gagauz.utils;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static boolean equalsToString(Object o1, Object o2) {
        return o1 == o2 || String.valueOf(o1).equals(String.valueOf(o2));
    }
}
