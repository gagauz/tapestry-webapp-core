package com.xl0e.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.xl0e.hibernate.model.base.Indexed;

public class CollectionUtils {

    public static <K extends Indexed> List<K> filterByMask(Collection<K> collection, Long mask) {
        List<K> result = C.arrayList(collection.size());

        for (K element : collection) {
            if ((mask & (1L << element.getIdx())) != 0x00) {
                result.add(element);
            }
        }

        return result;
    }

    public static <K extends Indexed> boolean hasElement(K element, Long mask) {
        return ((mask & (1L << element.getIdx())) != 0x00);
    }

    public static <K extends Indexed> long appendElement(K element, Long mask) {
        return (mask | (1L << element.getIdx()));
    }

    public static <K extends Indexed> long removeElement(K element, Long mask) {
        return (mask & (0x00 ^ (1L << element.getIdx())));
    }

    public static <K extends Indexed> List<K> filterByMask(Collection<K> collection,
                                                           Long mask,
                                                           Filter<K> filter) {
        List<K> result = C.arrayList(collection.size());

        for (K element : collection) {
            if (filter.apply(element) && (mask & (1L << element.getIdx())) != 0x00) {
                result.add(element);
            }
        }

        return result;
    }

    public static <T extends Indexed> long getMask(Collection<T> collection) {
        long result = 0;
        for (T t : collection) {
            result |= (1L << t.getIdx());
        }
        return result;
    }

    public static void main(String[] args) {
        // 275146342400
        // 275146342400
        // trig 38
        // sopr 28
        long l = 1L << 38;
        l = l | (1L << 28);
        System.out.println(l);
    }

    public static <T> Stream<T> stream(Collection<T> nullable) {
        return Optional.ofNullable(nullable).map(Collection::stream).orElse(Collections.<T>emptyList().stream());
    }

    public static <T> Collection<T> emptyIfNull(Collection<T> nullable) {
        if (null != nullable) {
            return nullable;
        }
        return Collections.<T>emptyList();
    }

}
