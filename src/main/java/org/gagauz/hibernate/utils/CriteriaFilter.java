package org.gagauz.hibernate.utils;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;

public class CriteriaFilter<T> extends EntityFilter<T> {
    private final Criteria criteria;
    private final Class<T> entityClass;

    public CriteriaFilter(Criteria criteria, Class<T> entityClass) {
        this.criteria = criteria;
        this.entityClass = entityClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> list() throws HibernateException {
        return setCriteria(criteria, entityClass).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T uniqueResult() throws HibernateException {
        return (T) setCriteria(criteria, entityClass).uniqueResult();
    }

    @Override
    public Long count() throws HibernateException {
        return (Long) setCriteria(criteria, entityClass).setProjection(Projections.rowCount()).uniqueResult();
    }
}
