package com.xl0e.hibernate.utils;

import org.hibernate.Query;
import org.hibernate.type.ObjectType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.math.BigDecimal;
import java.util.Date;

public class QueryParameter<T> {

    protected static final Type nullType = ObjectType.INSTANCE;

    protected final String name;
    protected final T value;
    protected final Type type;

    public QueryParameter(final String name, final T value) {
        this(name, value, null);
    }

    public QueryParameter(final String name, final T value, final Type type) {
        if (name == null) {
            throw new IllegalArgumentException("QueryParameter. Name of the parameter is null!");
        }
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public static <T> QueryParameter<T> param(String name, T value) {
        return new QueryParameter<T>(name, value);
    }

    public static QueryParameter<String> stringParam(String name, String value) {
        return param(name, value, StandardBasicTypes.STRING);
    }

    public static QueryParameter<Byte> byteParam(String name, Byte value) {
        return param(name, value, StandardBasicTypes.BYTE);
    }

    public static QueryParameter<Integer> intParam(String name, Integer value) {
        return param(name, value, StandardBasicTypes.INTEGER);
    }

    public static QueryParameter<Long> longParam(String name, Long value) {
        return param(name, value, StandardBasicTypes.LONG);
    }

    public static QueryParameter<Boolean> boolParam(String name, Boolean value) {
        return param(name, value, StandardBasicTypes.BOOLEAN);
    }

    public static QueryParameter<BigDecimal> bigDecimalParam(String name, BigDecimal value) {
        return param(name, value, StandardBasicTypes.BIG_DECIMAL);
    }

    public static QueryParameter<Date> timestampParam(String name, Date value) {
        return param(name, value, StandardBasicTypes.TIMESTAMP);
    }

    public static <T> QueryParameter<T> param(String name, T value, Type type) {
        return new QueryParameter<T>(name, value, type);
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public Query updateQuery(final Query query) {
        Type type = this.type != null ? this.type : guessHibernateType();
        if (type == null) {
            throw new IllegalStateException("QueryParameter. Illegal state for Query. Can not determine hibernate type of parameter! Name: " + name
                    + ". Value: " + value);
        }
        return setParameter(query, type);
    }

    protected Type guessHibernateType() {
        return value != null ? guessHibernateType(value) : nullType;
    }

    protected Query setParameter(final Query query, final Type type) {
        return query.setParameter(name, value, type);
    }

    static <T> Type guessHibernateType(final T value) {
        Type type = null;
        if (value != null) {
            if (value.getClass().isAssignableFrom(String.class)) {
                type = StandardBasicTypes.STRING;
            } else if (value.getClass().isAssignableFrom(Byte.class)) {
                type = StandardBasicTypes.BYTE;
            } else if (value.getClass().isAssignableFrom(Short.class)) {
                type = StandardBasicTypes.SHORT;
            } else if (value.getClass().isAssignableFrom(Integer.class)) {
                type = StandardBasicTypes.INTEGER;
            } else if (value.getClass().isAssignableFrom(Long.class)) {
                type = StandardBasicTypes.LONG;
            } else if (value.getClass().isAssignableFrom(Date.class)) {
                type = StandardBasicTypes.TIMESTAMP;
            } else if (value.getClass().isAssignableFrom(Boolean.class)) {
                type = StandardBasicTypes.BOOLEAN;
            } else if (value.getClass().isAssignableFrom(BigDecimal.class)) {
                type = StandardBasicTypes.BIG_DECIMAL;
            }
        }
        return type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("QueryParameter <name=").append(name);
        sb.append(", value=").append(value);
        if (type != null) {
            sb.append(", type=").append(type);
        }
        sb.append('>');

        return sb.toString();
    }
}
