package org.gagauz.hibernate.utils;

import org.gagauz.utils.C;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.DistinctRootEntityResultTransformer;

import java.util.*;
import java.util.Map.Entry;

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
    private Map<String, OrderMode> orderBy = new LinkedHashMap<String, OrderMode>();
    private List<Alias> aliases = C.newArrayList();
    private List<Criterion> criterias = C.newArrayList();

    public EntityFilter in(String name, Collection<?> value) {
        criterias.add(Restrictions.in(name, value));
        return this;
    }

    public EntityFilter eq(String name, Object value) {
        criterias.add(Restrictions.eq(name, value));
        return this;
    }

    public EntityFilter ne(String name, Object value) {
        criterias.add(Restrictions.ne(name, value));
        return this;
    }

    public EntityFilter like(String name, Object value) {
        criterias.add(Restrictions.like(name, value));
        return this;
    }

    public EntityFilter ge(String name, Object value) {
        criterias.add(Restrictions.ge(name, value));
        return this;
    }

    public EntityFilter le(String name, Object value) {
        criterias.add(Restrictions.le(name, value));
        return this;
    }

    public EntityFilter gt(String name, Object value) {
        criterias.add(Restrictions.gt(name, value));
        return this;
    }

    public EntityFilter lt(String name, Object value) {
        criterias.add(Restrictions.lt(name, value));
        return this;
    }

    public EntityFilter between(String name, Object value1, Object value2) {
        criterias.add(Restrictions.between(name, value1, value2));
        return this;
    }

    public EntityFilter isNull(String name) {
        criterias.add(Restrictions.isNull(name));
        return this;
    }

    public EntityFilter isNotNull(String name) {
        criterias.add(Restrictions.isNotNull(name));
        return this;
    }

    public EntityFilter sql(String sql) {
        criterias.add(Restrictions.sqlRestriction(sql));
        return this;
    }

    public EntityFilter limit(int limit) {
        this.indexTo = limit;
        return this;
    }

    public EntityFilter limit(int from, int limit) {
        this.indexFrom = from;
        this.indexTo = from + limit;
        return this;
    }

    public EntityFilter addAlias(String path, String alias) {
        this.aliases.add(new Alias(path, alias));
        return this;
    }

    public EntityFilter orderAsc(String column) {
        this.orderBy.put(column, OrderMode.ASC);
        return this;
    }

    public EntityFilter orderDecs(String column) {
        this.orderBy.put(column, OrderMode.DESC);
        return this;
    }

    public Criteria setCriteria(Criteria criteria) {
        for (Alias a : aliases) {
            criteria.createAlias(a.path, a.alias);
        }

        for (Criterion c : criterias) {
            criteria.add(c);
        }

        if (indexTo > 0 && indexFrom >= 0) {
            criteria.setFirstResult(indexFrom);
            criteria.setMaxResults(indexTo - indexFrom + 1);
        } else if (indexTo > 0) {
            criteria.setMaxResults(indexTo);
        }

        for (Entry<String, OrderMode> p : orderBy.entrySet()) {
            criteria.addOrder(p.getValue() == OrderMode.ASC ? Order.asc(p.getKey()) : Order.desc(p
                    .getKey()));
        }

        return criteria.setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
    }

    public void clearIndex() {
        indexFrom = -1;
        indexTo = -1;
    }

}
