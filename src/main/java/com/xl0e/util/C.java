package com.xl0e.util;

import java.lang.reflect.Array;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.xl0e.util.multimap.ListMultimap;
import com.xl0e.util.multimap.Multimaps;
import com.xl0e.util.multimap.SetMultimap;

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
        for (int i = 0; i
                + 1 < keyAndValues.length; i = i
                        + 2)
            map.put((K) keyAndValues[i], (V) keyAndValues[i
                    + 1]);
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

    public static <K, V, Z> ListMultimap<K, Z> listMultiMap(Collection<V> values,
                                                            Function<V, K> keyFunc,
                                                            Function<V, Z> valueFunc) {
        ListMultimap<K, Z> result = Multimaps.newArrayListMultimap();
        for (V v : values) {
            result.put(keyFunc.call(v), valueFunc.call(v));
        }
        return result;
    }

    public static <K, V, Z> ListMultimap<K, Z> listSortedMultiMap(Collection<V> values,
                                                                  Function<V, K> keyFunc,
                                                                  Function<V, Z> valueFunc) {
        ListMultimap<K, Z> result = Multimaps.newArrayListSortedMultimap();
        for (V v : values) {
            result.put(keyFunc.call(v), valueFunc.call(v));
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

    public static <T> Collection<T> emptyIfNull(Collection<T> iterable) {
        return null == iterable
                ? Collections.emptyList()
                : iterable;
    }

    public static <T> void unwrap(T element, Function<T, T> unwrapper) {
        T next = element;
        while (null != next) {
            next = unwrapper.call(next);
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
        return Optional.ofNullable(original).map(HashSet::new).orElse(null);
    }

    public static <T, X extends Set<T>> X copy(final Set<T> original, Function<Set<T>, X> creator) {
        return null == original
                ? null
                : creator.call(original);
    }

    public static <T> List<T> copy(final List<T> original) {
        return Optional.ofNullable(original).map(ArrayList::new).orElse(null);
    }

    public static <K, V> Map<K, V> copy(final Map<K, V> original) {
        return Optional.ofNullable(original).map(HashMap::new).orElse(null);
    }

}
