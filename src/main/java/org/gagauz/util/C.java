package org.gagauz.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.gagauz.util.multimap.ListMultimap;
import org.gagauz.util.multimap.Multimaps;
import org.gagauz.util.multimap.SetMultimap;

public class C {
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> source) {
        return new HashMap<>(source);
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet<>();
    }

    public static <E> HashSet<E> newHashSet(Collection<E> source) {
        return new HashSet<>(source);
    }

    public static <E> HashSet<E> newHashSet(E e, @SuppressWarnings("unchecked") E... source) {
        HashSet<E> set = new HashSet<>();
        set.add(e);
        set.addAll(Arrays.asList(source));
        return set;
    }

    public static <E> HashSet<E> hashSet(E[] source) {
        HashSet<E> set = new HashSet<>();
        if (null != source) {
            set.addAll(Arrays.asList(source));
        }
        return set;
    }

    public static <E> HashSet<E> hashSet() {
        HashSet<E> set = new HashSet<>();
        return set;
    }

    public static <E> ArrayList<E> arrayList() {
        return new ArrayList<>();
    }

    public static <E> ArrayList<E> arrayList(int capacity) {
        return new ArrayList<>(capacity);
    }

    public static <E> ArrayList<E> arrayList(Collection<E> source) {
        return new ArrayList<>(source);
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<>();
    }

    public static <E> LinkedList<E> newLinkedList(Collection<E> source) {
        return new LinkedList<>(source);
    }

    public static <P, V> Collection<V> transform(Collection<P> iterable, Function<P, V> adapter) {
        Collection<V> result = new ArrayList<>(iterable.size());
        for (P p : iterable) {
            result.add(adapter.call(p));
        }
        return result;
    }

    public static <P, V> Collection<V> transform(P[] iterable, Function<P, V> adapter) {
        ArrayList<V> result = new ArrayList<>(iterable.length);
        for (P p : iterable) {
            result.add(adapter.call(p));
        }
        return result;
    }

    public static <E> List<E> filter(E[] iterable, Filter<E> filter) {
        ArrayList<E> result = arrayList(iterable.length);
        for (E element : iterable) {
            if (filter.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <E> List<E> filter(Collection<E> iterable, Filter<E> filter) {
        ArrayList<E> result = arrayList(iterable.size());
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

    public static <E> E find(E[] iterable, Filter<E> filter) {
        for (E element : iterable) {
            if (filter.apply(element)) {
                return element;
            }
        }
        return null;
    }

    // public static <E> E find(Collection<E> iterable, Filter<E> filter) {
    // for (E element : iterable) {
    // if (filter.apply(element)) {
    // return element;
    // }
    // }
    // return null;
    // }

    public static <E> boolean has(Iterable<E> iterable, Filter<E> filter) {
        for (E element : iterable) {
            if (filter.apply(element)) {
                return true;
            }
        }
        return false;
    }

    public static <E> boolean has(Iterable<E> iterable, E element) {
        for (E e : iterable) {
            if (Objects.equals(e, element)) {
                return true;
            }
        }
        return false;
    }

    public static <E> boolean has(E[] iterable, E element) {
        if (null != iterable) {
            for (E e : iterable) {
                if (Objects.equals(e, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> HashMap<K, V> hashMap(Object... keyAndValues) {
        HashMap<K, V> map = newHashMap();
        for (int i = 0; i + 1 < keyAndValues.length; i = i + 2)
            map.put((K) keyAndValues[i], (V) keyAndValues[i + 1]);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(Collection<V> values, Function<V, K> keyFunc) {
        Map<K, V> result = newHashMap();
        for (V v : values) {
            result.put(keyFunc.call(v), v);
        }
        return result;
    }

    public static <K, V, Z> Map<K, Z> hashMap(Collection<V> values, Function<V, K> keyFunc, Function<V, Z> valueFunc) {
        Map<K, Z> result = newHashMap();
        for (V v : values) {
            result.put(keyFunc.call(v), valueFunc.call(v));
        }
        return result;
    }

    public static <K, V> ListMultimap<K, V> listMultiMap(Collection<V> values, Function<V, K> keyFunc) {
        ListMultimap<K, V> result = Multimaps.newArrayListMultimap();
        for (V v : values) {
            result.put(keyFunc.call(v), v);
        }
        return result;
    }

    public static <K, V> ListMultimap<K, V> listMultiMap() {
        ListMultimap<K, V> result = Multimaps.newArrayListMultimap();
        return result;
    }

    public static <K, V> SetMultimap<K, V> setMultiMap() {
        SetMultimap<K, V> result = Multimaps.newHashMultimap();
        return result;
    }

    public static <K, V, Z> ListMultimap<K, Z> listMultiMap(Collection<V> values, Function<V, K> keyFunc,
            Function<V, Z> valueFunc) {
        ListMultimap<K, Z> result = Multimaps.newArrayListMultimap();
        for (V v : values) {
            result.put(keyFunc.call(v), valueFunc.call(v));
        }
        return result;
    }

    public static <K, V, Z> ListMultimap<K, Z> listSortedMultiMap(Collection<V> values, Function<V, K> keyFunc,
            Function<V, Z> valueFunc) {
        ListMultimap<K, Z> result = Multimaps.newArrayListSortedMultimap();
        for (V v : values) {
            result.put(keyFunc.call(v), valueFunc.call(v));
        }
        return result;
    }

    public static <T extends Comparable<T>> int compare(T a, T b) {
        if (null != a && null != b) {
            return a.compareTo(b);
        }
        return 0;
    }

    public static <T> T first(Iterable<T> iterable) {
        if (null != iterable) {
            for (T element : iterable) {
                return element;
            }
        }
        return null;
    }

    public static <T> void forEach(Iterable<T> iterable, Consumer<T> consumer) {
        if (null != iterable) {
            for (T t : iterable) {
                consumer.accept(t);
            }
        }
    }

    public static <T> void forEach(T[] array, Consumer<T> consumer) {
        if (null != array) {
            for (int i = 0; i < array.length; i++)
                consumer.accept(array[i]);
        }
    }

    public static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
        return null == iterable ? Collections.emptyList() : iterable;
    }
}
