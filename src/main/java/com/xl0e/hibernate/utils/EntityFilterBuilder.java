package com.xl0e.hibernate.utils;

import java.util.Collection;

public class EntityFilterBuilder {

    private EntityFilterBuilder() {
    }

    public static EntityFilter and() {
        return new EntityFilter().and();
    }

    public static EntityFilter or() {
        return new EntityFilter().or();
    }

    public static EntityFilter in(String name, Collection<?> value) {
        return new EntityFilter().in(name, value);
    }

    public static EntityFilter eq(String name, Object value) {
        return new EntityFilter().eq(name, value);
    }

    public static EntityFilter ne(String name, Object value) {
        return new EntityFilter().ne(name, value);
    }

    public static EntityFilter like(String name, Object value) {
        return new EntityFilter().like(name, value);
    }

    public static EntityFilter ge(String name, Object value) {
        return new EntityFilter().ge(name, value);
    }

    public static EntityFilter le(String name, Object value) {
        return new EntityFilter().le(name, value);
    }

    public static EntityFilter gt(String name, Object value) {
        return new EntityFilter().gt(name, value);
    }

    public static EntityFilter lt(String name, Object value) {
        return new EntityFilter().lt(name, value);
    }

    public static EntityFilter between(String name, Object value1, Object value2) {
        return new EntityFilter().between(name, value1, value2);
    }

    public static EntityFilter isNull(String name) {
        return new EntityFilter().isNull(name);
    }

    public static EntityFilter isNotNull(String name) {
        return new EntityFilter().isNotNull(name);
    }

    public static EntityFilter sql(String sql) {
        return new EntityFilter().sql(sql);
    }

    public static EntityFilter limit(int limit) {
        return new EntityFilter().limit(limit);
    }

    public static EntityFilter limit(int from, int limit) {
        return new EntityFilter().limit(from, limit);
    }

    public static EntityFilter addAlias(String path, String alias) {
        return new EntityFilter().addAlias(path, alias);
    }

    public static EntityFilter orderAsc(String column) {
        return new EntityFilter().orderAsc(column);
    }

    public static EntityFilter orderDecs(String column) {
        return new EntityFilter().orderDecs(column);
    }

}
