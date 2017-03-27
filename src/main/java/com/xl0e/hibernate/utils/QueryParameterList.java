package com.xl0e.hibernate.utils;

import org.hibernate.Query;
import org.hibernate.type.Type;

import java.util.Arrays;
import java.util.Collection;

public class QueryParameterList<T> extends QueryParameter<Collection<T>> {

    public QueryParameterList(String name, Collection<T> values) {
        this(name, values, null);
    }

    public QueryParameterList(String name, T... values) {
        this(name, null, values);
    }

    public QueryParameterList(String name, Type type, T... values) {
        this(name, Arrays.asList(values), type);
    }

    public QueryParameterList(String name, Collection<T> values, Type type) {
        super(name, values, type);
        if (values == null) {
            throw new IllegalArgumentException("QueryParameterList. Collection shoud not be null!");
        }
    }

    public static <T> QueryParameterList<T> paramList(String name, Collection<T> values) {
        return new QueryParameterList<T>(name, values);
    }

    public static <T> QueryParameterList<T> paramList(String name, T... values) {
        return new QueryParameterList<T>(name, values);
    }

    public static <T> QueryParameterList<T> paramList(String name, Type type, T... values) {
        return new QueryParameterList<T>(name, type, values);
    }

    public static <T> QueryParameterList<T> paramList(String name, Collection<T> values, Type type) {
        return new QueryParameterList<T>(name, values, type);
    }

    @Override
    public Query setParameter(final Query query, final Type type) {
        return query.setParameterList(name, value, type);
    }

    @Override
    protected Type guessHibernateType() {
        return value.size() != 0 ? guessHibernateType(value.iterator().next()) : nullType;
    }
}
