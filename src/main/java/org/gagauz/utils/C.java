package org.gagauz.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class C {
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> source) {
        return new HashMap<K, V>(source);
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    public static <E> HashSet<E> newHashSet(Collection<E> source) {
        return new HashSet<E>(source);
    }

    public static <E> HashSet<E> newHashSet(E e, @SuppressWarnings("unchecked") E... source) {
        HashSet<E> set = new HashSet<E>();
        set.add(e);
        set.addAll(Arrays.asList(source));
        return set;
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    public static <E> ArrayList<E> newArrayList(int capacity) {
        return new ArrayList<E>(capacity);
    }

    public static <E> ArrayList<E> newArrayList(Collection<E> source) {
        return new ArrayList<E>(source);
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }

    public static <E> LinkedList<E> newLinkedList(Collection<E> source) {
        return new LinkedList<E>(source);
    }

    public static <P, V> Collection<V> transform(Collection<P> iterable, Function<P, V> adapter) {
        Collection<V> result = new ArrayList<V>(iterable.size());
        for (P p : iterable) {
            result.add(adapter.call(p));
        }
        return result;
    }

    public static <P, V> Collection<V> transform(P[] iterable, Function<P, V> adapter) {
        ArrayList<V> result = new ArrayList<V>(iterable.length);
        for (P p : iterable) {
            result.add(adapter.call(p));
        }
        return result;
    }

    public static <E> List<E> filter(E[] iterable, Filter<E> filter) {
        ArrayList<E> result = newArrayList(iterable.length);
        for (E element : iterable) {
            if (filter.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <E> List<E> filter(Collection<E> iterable, Filter<E> filter) {
        ArrayList<E> result = newArrayList(iterable.size());
        for (E element : iterable) {
            if (filter.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <E> E find(Iterable<E> iterable, Filter<E> filter) {
        for (E element : iterable) {
            if (filter.apply(element)) {
                return element;
            }
        }
        return null;
    }

    public static <E> E find(Collection<E> iterable, Filter<E> filter) {
        for (E element : iterable) {
            if (filter.apply(element)) {
                return element;
            }
        }
        return null;
    }

    public static <E> boolean has(Collection<E> iterable, Filter<E> filter) {
        for (E element : iterable) {
            if (filter.apply(element)) {
                return true;
            }
        }
        return false;
    }

    public static Map<Object, Object> newHashMap(Object... keyAndValues) {
        Map<Object, Object> map = newHashMap();
        for (int i = 0; i + 1 < keyAndValues.length; i = i + 2)
            map.put(keyAndValues[i], keyAndValues[i + 1]);
        return map;
    }
}
