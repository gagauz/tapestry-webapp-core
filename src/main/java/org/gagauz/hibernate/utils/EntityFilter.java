package org.gagauz.hibernate.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gagauz.utils.C;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

public class EntityFilter {
    public static enum OrderMode {
        ASC,
        DESC
    }

    public static class Or extends Disjunction {
        Or() {
            super();
        }
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
    private List<Projection> projections = C.arrayList();
    private List<Criterion> criterias = C.arrayList();

    private Junction mode = new Conjunction();

    public EntityFilter or() {
        if (mode.conditions().iterator().hasNext()) {
            criterias.add(mode);
        }
        mode = new Or();
        return this;
    }

    public EntityFilter and() {
        if (mode.conditions().iterator().hasNext()) {
            criterias.add(mode);
        }
        mode = new Conjunction();
        return this;
    }

    public EntityFilter in(String name, Collection<?> value) {
        mode.add(Restrictions.in(name, value));
        return this;
    }

    public EntityFilter in(String name, Object value, Class<?> clazz) {
        DetachedCriteria dc = DetachedCriteria.forClass(clazz);
        dc.add(Restrictions.eq(name, value));
        mode.add(Subqueries.exists(dc));
        return this;
    }

    public EntityFilter eq(String name, Object value) {
        mode.add(Restrictions.eq(name, value));
        return this;
    }

    public EntityFilter ne(String name, Object value) {
        mode.add(Restrictions.ne(name, value));
        return this;
    }

    public EntityFilter like(String name, Object value) {
        mode.add(Restrictions.like(name, value));
        return this;
    }

    public EntityFilter ge(String name, Object value) {
        mode.add(Restrictions.ge(name, value));
        return this;
    }

    public EntityFilter le(String name, Object value) {
        mode.add(Restrictions.le(name, value));
        return this;
    }

    public EntityFilter gt(String name, Object value) {
        mode.add(Restrictions.gt(name, value));
        return this;
    }

    public EntityFilter lt(String name, Object value) {
        mode.add(Restrictions.lt(name, value));
        return this;
    }

    public EntityFilter between(String name, Object value1, Object value2) {
        mode.add(Restrictions.between(name, value1, value2));
        return this;
    }

    public EntityFilter isNull(String name) {
        mode.add(Restrictions.isNull(name));
        return this;
    }

    public EntityFilter isNotNull(String name) {
        mode.add(Restrictions.isNotNull(name));
        return this;
    }

    public EntityFilter sql(String sql) {
        mode.add(Restrictions.sqlRestriction(sql));
        return this;
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

    public EntityFilter projection(Projection projection) {
        projections.add(projection);
        return this;
    }

    public EntityFilter groupBy(String string) {
        projections.add(Projections.groupProperty(string));
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

    public Criteria setCriteria(Criteria source, Class<?> bean) {
        for (Alias a : aliases) {
            source.createAlias(a.path, a.alias);
        }
        if (!projections.isEmpty()) {
            ProjectionList plist = Projections.projectionList();
            for (Projection p : projections) {
                plist.add(p);
            }
            source.setProjection(plist);
            source.setResultTransformer(Transformers.aliasToBean(bean));
        }

        and(); // Finish sequence
        criterias.forEach(c -> source.add(c));

        if (indexTo > 0 && indexFrom >= 0) {
            source.setFirstResult(indexFrom);
            source.setMaxResults(indexTo - indexFrom + 1);
        } else if (indexTo > 0) {
            source.setMaxResults(indexTo);
        }

        for (Entry<String, OrderMode> p : orderBy.entrySet()) {
            source.addOrder(p.getValue() == OrderMode.ASC ? Order.asc(p.getKey()) : Order.desc(p.getKey()));
        }

        return source;// .setResultTransformer(DistinctRootEntityResultTransformer.INSTANCE);
    }

    public void clearIndex() {
        indexFrom = -1;
        indexTo = -1;
    }

}
