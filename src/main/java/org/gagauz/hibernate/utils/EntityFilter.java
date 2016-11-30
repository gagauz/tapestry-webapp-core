package org.gagauz.hibernate.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gagauz.utils.C;
import org.gagauz.utils.Function;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.DistinctRootEntityResultTransformer;


public class EntityFilter {
    public static enum OrderMode {
        ASC,
        DESC
    }

    static class Alias {
        Alias(String path, String alias) {
            this.alias = alias;
            this.path = path;
        }

        String alias;
        String path;
    }

    protected int indexFrom = -1;
    protected int indexTo = -1;
    private Map<String, OrderMode> orderBy = new LinkedHashMap<>();
    private List<Alias> aliases = C.arrayList();
    private Criterion criteria = null;

    private final Function<Criterion, EntityFilter> OR = new Function<Criterion, EntityFilter>() {

        @Override
        public EntityFilter call(Criterion criterion) {
            or(criterion);
            return EntityFilter.this;
        }
    };

    private final Function<Criterion, EntityFilter> AND = new Function<Criterion, EntityFilter>() {

        @Override
        public EntityFilter call(Criterion criterion) {
            and(criterion);
            return EntityFilter.this;
        }
    };

    private Function<Criterion, EntityFilter> mode = AND;

    public EntityFilter or() {
        mode = OR;
        return this;
    }

    public EntityFilter and() {
        mode = AND;
        return this;
    }

    private void and(Criterion criterion) {
        if (null == criteria) {
            criteria = criterion;
        } else {
            criteria = Restrictions.and(criteria, criterion);
        }
    }

    private void or(Criterion criterion) {
        if (null == criteria) {
            criteria = criterion;
        } else {
            criteria = Restrictions.or(criteria, criterion);
        }
    }

    public EntityFilter in(String name, Collection<?> value) {
        return mode.call(Restrictions.in(name, value));
    }

    public EntityFilter in(String name, Object value, Class<?> clazz) {
        DetachedCriteria dc = DetachedCriteria.forClass(clazz);
        dc.add(Restrictions.eq(name, value));
        return mode.call(Subqueries.exists(dc));
    }

    public EntityFilter eq(String name, Object value) {
        return mode.call(Restrictions.eq(name, value));
    }

    public EntityFilter ne(String name, Object value) {
        return mode.call(Restrictions.ne(name, value));
    }

    public EntityFilter like(String name, Object value) {
        return mode.call(Restrictions.like(name, value));
    }

    public EntityFilter ge(String name, Object value) {
        return mode.call(Restrictions.ge(name, value));
    }

    public EntityFilter le(String name, Object value) {
        return mode.call(Restrictions.le(name, value));
    }

    public EntityFilter gt(String name, Object value) {
        return mode.call(Restrictions.gt(name, value));
    }

    public EntityFilter lt(String name, Object value) {
        return mode.call(Restrictions.lt(name, value));
    }

    public EntityFilter between(String name, Object value1, Object value2) {
        return mode.call(Restrictions.between(name, value1, value2));
    }

    public EntityFilter isNull(String name) {
        return mode.call(Restrictions.isNull(name));
    }

    public EntityFilter isNotNull(String name) {
        return mode.call(Restrictions.isNotNull(name));
    }

    public EntityFilter sql(String sql) {
        return mode.call(Restrictions.sqlRestriction(sql));
    }

    public EntityFilter limit(int limit) {
        indexTo = limit;
        return this;
    }

    public EntityFilter limit(int from, int limit) {
        indexFrom = from;
        indexTo = from + limit;
        return this;
    }

    public EntityFilter addAlias(String path, String alias) {
        aliases.add(new Alias(path, alias));
        return this;
    }

    public EntityFilter orderAsc(String column) {
        orderBy.put(column, OrderMode.ASC);
        return this;
    }

    public EntityFilter orderDecs(String column) {
        orderBy.put(column, OrderMode.DESC);
        return this;
    }

    public Criteria setCriteria(Criteria source) {
        for (Alias a : aliases) {
            source.createAlias(a.path, a.alias);
        }

        if (null != criteria) {
            source.add(criteria);
        }

        if (indexTo > 0 && indexFrom >= 0) {
            source.setFirstResult(indexFrom);
            source.setMaxResults(indexTo - indexFrom + 1);
        } else if (indexTo > 0) {
            source.setMaxResults(indexTo);
        }

        for (Entry<String, OrderMode> p : orderBy.entrySet()) {
            source.addOrder(p.getValue() == OrderMode.ASC ? Order.asc(p.getKey()) : Order.desc(p.getKey()));
        }

        return source.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
    }

    public void clearIndex() {
        indexFrom = -1;
        indexTo = -1;
    }

}
