package org.gagauz.utils.multimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class Multimaps {

    private Multimaps() {
    }

    public static <K, V> SetMultimap<K, V> newHashMultimap() {
        return new HashMultimap<>(new HashMap<K, Set<V>>());
    }

    public static <K, V> SetMultimap<K, V> newHashMultimap(Map<K, Set<V>> map) {
        return new HashMultimap<>(new HashMap<>(map));
    }

    public static <K, V> SetMultimap<K, V> newLinkedHashMultimap() {
        return newLinkedHashMultimap(new HashMap<K, Set<V>>());
    }

    public static <K, V> SetMultimap<K, V> newLinkedHashMultimap(Map<K, Set<V>> map) {
        return new LinkedHashMultimap<>(map);
    }

    public static <K, V> ListMultimap<K, V> newArrayListMultimap() {
        return newArrayListMultimap(new HashMap<K, List<V>>());
    }

    public static <K, V> ListMultimap<K, V> newArrayListSortedMultimap() {
        return newArrayListMultimap(new TreeMap<K, List<V>>());
    }

    public static <K, V> ListMultimap<K, V> newArrayListMultimap(Map<K, List<V>> map) {
        return new ArrayListMultimap<>(map);
    }

    public static <K, V> ListMultimap<K, V> newLinkedListMultimap() {
        return new LinkedListMultimap<>(new HashMap<K, List<V>>());
    }

    public static <K, V> ListMultimap<K, V> newLinkedListMultimap(Map<K, List<V>> map) {
        return new LinkedListMultimap<>(new HashMap<>(map));
    }

    private static final class ArrayListMultimap<K, V> extends
            AbstractListMultimap<K, V, List<V>> {
        private static final long serialVersionUID = 6576158185357912854L;

        private ArrayListMultimap(Map<K, List<V>> map) {
            super(map);
        }

        @Override
        List<V> createCollection() {
            return new ArrayList<>();
        }
    }

    private static final class LinkedListMultimap<K, V> extends
            AbstractListMultimap<K, V, List<V>> {
        private static final long serialVersionUID = 1L;

        private LinkedListMultimap(Map<K, List<V>> map) {
            super(map);
        }

        @Override
        List<V> createCollection() {
            return new LinkedList<>();
        }
    }

    private static final class HashMultimap<K, V> extends
            AbstractMultimap<K, V, Set<V>> implements SetMultimap<K, V> {
        private static final long serialVersionUID = -5662122782595891207L;

        private HashMultimap(Map<K, Set<V>> map) {
            super(map);
        }

        @Override
        Set<V> createCollection() {
            return new HashSet<>();
        }

        @Override
        public Map<K, Set<V>> asMap() {
            return createMap();
        }
    }

    private static final class LinkedHashMultimap<K, V> extends
            AbstractMultimap<K, V, Set<V>> implements SetMultimap<K, V> {
        private static final long serialVersionUID = 8947127701020167995L;

        private LinkedHashMultimap(Map<K, Set<V>> map) {
            super(map);
        }

        @Override
        Set<V> createCollection() {
            return new LinkedHashSet<>();
        }

        @Override
        public Map<K, Set<V>> asMap() {
            return createMap();
        }
    }

    private abstract static class AbstractListMultimap<K, V, C extends List<V>> extends AbstractMultimap<K, V, C>
            implements ListMultimap<K, V> {
        private static final long serialVersionUID = -8521842722983271296L;

        protected AbstractListMultimap(Map<K, C> map) {
            super(map);
        }

        @Override
        public Map<K, List<V>> asMap() {
            return (Map<K, List<V>>) createMap();
        }
    }
}
