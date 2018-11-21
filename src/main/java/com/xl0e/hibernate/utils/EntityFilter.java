package com.xl0e.hibernate.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import com.xl0e.util.C;

public class EntityFilter<T> {
	public static enum OrderMode {
		ASC,
		DESC
	}

	public static class Or extends Junction {
		Or() {
			super(Nature.OR);
		}
	}

	public static class And extends Junction {
		And() {
			super(Nature.AND);
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
	protected int maxResult = -1;
	private Map<String, OrderMode> orderBy = new LinkedHashMap<>();
	private List<Alias> aliases = C.arrayList();
	private List<Projection> projections = C.arrayList();
	private List<Criterion> criterias = C.arrayList();

	private Junction mode = new And();

	public EntityFilter<T> append(EntityFilter<T> filter) {

		indexFrom = Math.max(indexFrom, filter.indexFrom);
		maxResult = Math.max(maxResult, filter.maxResult);
		orderBy.putAll(filter.orderBy);
		aliases.addAll(filter.aliases);
		projections.addAll(filter.projections);
		criterias.addAll(filter.criterias);
		C.forEach(filter.mode.conditions(), mode::add);

		return this;
	}

	public EntityFilter<T> or() {
		// if (mode.conditions().iterator().hasNext()) {
		// criterias.add(mode);
		// }
		criterias.add(mode);
		mode = new Or();
		return this;
	}

	public EntityFilter<T> and() {
		// if (mode.conditions().iterator().hasNext()) {
		// criterias.add(mode);
		// }
		criterias.add(mode);
		mode = new And();
		return this;
	}

	public EntityFilter<T> in(String name, Collection<?> value) {
		mode.add(Restrictions.in(name, value));
		return this;

	}

	public EntityFilter<T> in(String name, Object value, Class<?> clazz) {
		DetachedCriteria dc = DetachedCriteria.forClass(clazz);
		dc.add(Restrictions.eq(name, value));
		mode.add(Subqueries.exists(dc));
		return this;
	}

	public EntityFilter<T> eq(String name, Object value) {
		mode.add(Restrictions.eq(name, value));
		return this;
	}

	public EntityFilter<T> isTrue(String name) {
		mode.add(Restrictions.eq(name, true));
		return this;
	}

	public EntityFilter<T> isFalse(String name) {
		mode.add(Restrictions.eq(name, false));
		return this;
	}

	public EntityFilter<T> ne(String name, Object value) {
		mode.add(Restrictions.ne(name, value));
		return this;
	}

	public EntityFilter<T> like(String name, Object value) {
		mode.add(Restrictions.like(name, value));
		return this;
	}

	public EntityFilter<T> ge(String name, Object value) {
		mode.add(Restrictions.ge(name, value));
		return this;
	}

	public EntityFilter<T> le(String name, Object value) {
		mode.add(Restrictions.le(name, value));
		return this;
	}

	public EntityFilter<T> gt(String name, Object value) {
		mode.add(Restrictions.gt(name, value));
		return this;
	}

	public EntityFilter<T> lt(String name, Object value) {
		mode.add(Restrictions.lt(name, value));
		return this;
	}

	public EntityFilter<T> between(String name, Object value1, Object value2) {
		mode.add(Restrictions.between(name, value1, value2));
		return this;
	}

	public EntityFilter<T> isNull(String name) {
		mode.add(Restrictions.isNull(name));
		return this;
	}

	public EntityFilter<T> isNotNull(String name) {
		mode.add(Restrictions.isNotNull(name));
		return this;
	}

	public EntityFilter<T> sql(String sql) {
		mode.add(Restrictions.sqlRestriction(sql));
		return this;
	}

	public EntityFilter<T> limit(int limit) {
		maxResult = limit;
		return this;
	}

	public EntityFilter<T> limit(int from, int limit) {
		indexFrom = from;
		maxResult = limit;
		return this;
	}

	public EntityFilter<T> addAlias(String path, String alias) {
		aliases.add(new Alias(path, alias));
		return this;
	}

	public EntityFilter<T> projection(Projection projection) {
		projections.add(projection);
		return this;
	}

	public EntityFilter<T> groupBy(String string) {
		projections.add(Projections.groupProperty(string));
		return this;
	}

	public EntityFilter<T> orderAsc(String column) {
		orderBy.put(column, OrderMode.ASC);
		return this;
	}

	public EntityFilter<T> orderDecs(String column) {
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

		if (indexFrom > 0) {
			source.setFirstResult(indexFrom);
		}
		if (maxResult > 0) {
			source.setMaxResults(maxResult);
		}

		for (Entry<String, OrderMode> p : orderBy.entrySet()) {
			source.addOrder(p.getValue() == OrderMode.ASC ? Order.asc(p.getKey()) : Order.desc(p.getKey()));
		}

		return source;
	}

	public EntityFilter<T> clearOrder() {
		orderBy.clear();
		return this;
	}

	public EntityFilter<T> clearIndex() {
		indexFrom = -1;
		maxResult = -1;
		return this;
	}

	public List<T> list() throws HibernateException {
		throw new NoSuchMethodError();
	}

	public T uniqueResult() throws HibernateException {
		throw new NoSuchMethodError();
	}

	public Long count() throws HibernateException {
		throw new NoSuchMethodError();
	}
}
