package com.xl0e.util;

import java.util.EnumSet;

public class EnumUtils {
    public static <E extends Enum<E>> EnumSet<E> filter(Class<E> clazz, long mask) {
        EnumSet<E> result = EnumSet.noneOf(clazz);
        E[] all = clazz.getEnumConstants();
        for (E e : all) {
            if ((mask & (1L << e.ordinal())) != 0) {
                result.add(e);
            }
        }

        return result;
    }
}
