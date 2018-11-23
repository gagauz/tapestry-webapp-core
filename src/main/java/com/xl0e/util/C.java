package com.xl0e.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.xl0e.util.multimap.ListMultimap;
import com.xl0e.util.multimap.Multimaps;
import com.xl0e.util.multimap.SetMultimap;

public class C {
    public static <K, V> HashMap<K, V> hashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> hashMap(Map<K, V> source) {
        return new HashMap<>(source);
    }

    public static <K, V> LinkedHashMap<K, V> linkedHashMap() {
        return new LinkedHashMap<>();
    }

    public static <K, V> LinkedHashMap<K, V> linkedHashMap(Map<K, V> source) {
        return new LinkedHashMap<>(source);
    }

    public static <E> HashSet<E> hashSet() {
        return new HashSet<>();
    }

    public static <E> HashSet<E> hashSet(Collection<E> source) {
        return new HashSet<>(source);
    }

    public static <E> LinkedHashSet<E> linkedHashSet() {
        return new LinkedHashSet<>();
    }

    public static <E> LinkedHashSet<E> linkedHashSet(Collection<E> source) {
        return new LinkedHashSet<>(source);
    }

    public static <E> HashSet<E> hashSet(E e, @SuppressWarnings("unchecked") E... source) {
        HashSet<E> set = new HashSet<>();
        set.add(e);
        if (source.length > 0) {
            set.addAll(Arrays.asList(source));
        }
        return set;
    }

    public static <E> HashSet<E> hashSet(E[] source) {
        HashSet<E> set = new HashSet<>();
        if (null != source) {
            set.addAll(Arrays.asList(source));
        }
        return set;
    }

    public static <E> ArrayList<E> arrayList() {
        return new ArrayList<>();
    }

    public static <E> ArrayList<E> arrayList(int capacity) {
        return new ArrayList<>(capacity);
    }

    public static <E> ArrayList<E> arrayList(Collection<E> source) {
        if (null == source) {
            return arrayList();
        }
        return new ArrayList<>(source);
    }

    public static <E> LinkedList<E> linkedList() {
        return new LinkedList<>();
    }

    public static <E> LinkedList<E> linkedList(Collection<E> source) {
        return new LinkedList<>(source);
    }

    public static <P, V> Collection<V> transform(Collection<P> iterable, Function<P, V> adapter) {
        Collection<V> result = new ArrayList<>(iterable.size());
        for (P p : iterable) {
            result.add(adapter.apply(p));
        }
        return result;
    }

    public static <P, V> Collection<V> transform(P[] iterable, Function<P, V> adapter) {
        ArrayList<V> result = new ArrayList<>(iterable.length);
        for (P p : iterable) {
            result.add(adapter.apply(p));
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
        HashMap<K, V> map = hashMap();
        for (int i = 0; i
                + 1 < keyAndValues.length; i = i
                        + 2)
            map.put((K) keyAndValues[i], (V) keyAndValues[i
                    + 1]);
        return map;
    }

    public static <K, V> Map<K, V> hashMap(Collection<V> values, Function<V, K> keyFunc) {
        Map<K, V> result = hashMap();
        for (V v : values) {
            result.put(keyFunc.apply(v), v);
        }
        return result;
    }

    public static <K, V, Z> Map<K, Z> hashMap(Collection<V> values, Function<V, K> keyFunc, Function<V, Z> valueFunc) {
        Map<K, Z> result = hashMap();
        for (V v : values) {
            result.put(keyFunc.apply(v), valueFunc.apply(v));
        }
        return result;
    }

    public static <K, V> ListMultimap<K, V> listMultiMap(Collection<V> values, Function<V, K> keyFunc) {
        ListMultimap<K, V> result = Multimaps.newArrayListMultimap();
        for (V v : values) {
            result.put(keyFunc.apply(v), v);
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

    public static <K, V, Z> ListMultimap<K, Z> listMultiMap(Collection<V> values,
            Function<V, K> keyFunc,
            Function<V, Z> valueFunc) {
        ListMultimap<K, Z> result = Multimaps.newArrayListMultimap();
        for (V v : values) {
            result.put(keyFunc.apply(v), valueFunc.apply(v));
        }
        return result;
    }

    public static <K, V, Z> ListMultimap<K, Z> listSortedMultiMap(Collection<V> values,
            Function<V, K> keyFunc,
            Function<V, Z> valueFunc) {
        ListMultimap<K, Z> result = Multimaps.newArrayListSortedMultimap();
        for (V v : values) {
            result.put(keyFunc.apply(v), valueFunc.apply(v));
        }
        return result;
    }

    public static <T extends Comparable<T>> int compare(T a, T b) {
        if (null != a
                && null != b) {
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

    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return null == collection
                ? Collections.emptyList()
                : collection;
    }

    public static <T> List<T> emptyIfNull(List<T> collection) {
        return null == collection
                ? Collections.emptyList()
                : collection;
    }

    public static <T> Set<T> emptyIfNull(Set<T> iterable) {
        return null == iterable
                ? Collections.emptySet()
                : iterable;
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return null == map
                ? Collections.emptyMap()
                : map;
    }

    public static <T> void unwrap(T element, Function<T, T> unwrapper) {
        T next = element;
        while (null != next) {
            next = unwrapper.apply(next);
        }
    }

    public static <T> T ifNull(final T value, final T elseValue) {
        if (null == value) {
            return elseValue;
        }
        return value;
    }

    public static <T> T ifNull(final T value, final Supplier<T> supplier) {
        if (null == value) {
            return supplier.get();
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] addToArray(final T[] array, final T value) {
        if (null == array) {
            if (null == value) {
                return (T[]) new Object[] { value };
            }
            T[] arr = (T[]) Array.newInstance(value.getClass(), 1);
            arr[0] = value;
            return arr;
        }
        T[] copy = Arrays.copyOf(array, array.length
                + 1);
        copy[array.length] = value;
        return copy;
    }

    public static <T> Set<T> copy(final Set<T> original) {
        return null == original
                ? null
                : original instanceof LinkedHashSet
                        ? linkedHashSet(original)
                        : hashSet(original);

    }

    public static <T, X extends Set<T>> X copy(final Set<T> original, Function<Set<T>, X> creator) {
        return null == original
                ? null
                : creator.apply(original);
    }

    public static <T> List<T> copy(final List<T> original) {
        return null == original
                ? null
                : original instanceof LinkedList
                        ? linkedList(original)
                        : arrayList(original);

    }

    public static <K, V> Map<K, V> copy(final Map<K, V> original) {
        return null == original
                ? null
                : original instanceof LinkedHashMap
                        ? linkedHashMap(original)
                        : hashMap(original);
    }

    public static <X> boolean isNotEmpty(Collection<X> collection) {
        return null != collection && !collection.isEmpty();
    }

    public static <X> boolean isNotEmpty(X[] array) {
        return null != array && array.length > 0;
    }

    public static <T, K> List<T> sortAgainst(final List<T> list,
            final Function<T, K> getter,
            final List<K> mirror) {
        final LinkedHashMap<K, T> map = new LinkedHashMap<>(mirror.size());
        mirror.forEach(k -> map.put(k, null));
        list.forEach(t -> {
            map.put(getter.apply(t), t);
        });
        return C.arrayList(map.values());
    }

    public static String[] trim(String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = StringUtils.trim(array[i]);
        }
        return array;

    }

    public static <X> boolean isEmpty(Collection<X> collection) {
        return null == collection || collection.isEmpty();
    }

}
