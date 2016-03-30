package org.gagauz.hibernate.dao;

import org.gagauz.hibernate.utils.EntityFilter;
import org.gagauz.hibernate.utils.HqlEntityFilter;
import org.gagauz.hibernate.utils.QueryParameter;
import org.gagauz.utils.Function;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class AbstractDao<Id extends Serializable, Entity> {

    @SuppressWarnings("rawtypes")
    public static final Map<Class, AbstractDao> DAO_MAP = new HashMap<Class, AbstractDao>();

    @Autowired
    private SessionFactory sessionFactory;

    public final Class<Id> idClass;
    public final Class<Entity> entityClass;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        Type[] parameters = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        idClass = (Class<Id>) parameters[0];
        entityClass = (Class<Entity>) parameters[1];
        DAO_MAP.put(entityClass, this);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void setSession(Session session) {

    }

    @SuppressWarnings("unchecked")
    public Id getIdentifier(Entity entity) {
        return (Id) getSession().getIdentifier(entity);
    }

    @SuppressWarnings("unchecked")
    public Entity findById(Id id) {
        return (Entity) getSession().get(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    public Entity loadById(Id id) {
        return (Entity) getSession().load(entityClass, id);
    }

    @SuppressWarnings("unchecked")
    public List<Entity> findByIds(Collection<Id> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return getSession().createCriteria(entityClass).add(Restrictions.in("id", ids)).list();
    }

    public Query createQuery(String queryString) {
        return getSession().createQuery(queryString);
    }

    public SQLQuery createSQLQuery(String queryString) {
        return getSession().createSQLQuery(queryString);
    }

    @SuppressWarnings("unchecked")
    public List<Entity> findAll() {
        return getSession().createCriteria(entityClass).list();
    }

    @SuppressWarnings("unchecked")
    public List<Entity> findByFilter(final EntityFilter filter) {
        return filter.setCriteria(getSession().createCriteria(entityClass)).list();
    }

    @SuppressWarnings("unchecked")
    public Entity findOneByFilter(final EntityFilter filter) {
        return (Entity) filter.setCriteria(getSession().createCriteria(entityClass)).uniqueResult();
    }

    public long countByFilter(final EntityFilter filter) {
        return (Long) filter.setCriteria(getSession().createCriteria(entityClass).setProjection(Projections.rowCount())).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Entity> findByFilter(final String sql, final HqlEntityFilter filter) {
        Query query = filter.createQuery(new Function<String, Query>() {
            @Override
            public Query call(String arg0) {
                return createQuery(arg0);
            }
        }, sql);

        return query.list();
    }

    public Criteria createCriteria() {
        return getSession().createCriteria(entityClass);
    }

    @SuppressWarnings("rawtypes")
    public List findByQuery(String hql, QueryParameter... params) {
        Query query = getSession().createQuery(hql);
        for (QueryParameter param : params) {
            param.updateQuery(query);
        }
        return query.list();
    }

    public void merge(Entity entity) {
        getSession().merge(entity);
    }

    public void saveNoCommit(Entity entity) {
        getSession().saveOrUpdate(entity);
    }

    public void save(Entity entity) {
        getSession().saveOrUpdate(entity);
        getSession().flush();
    }

    public void save(Collection<Entity> entities) {
        for (Entity entity : entities) {
            getSession().saveOrUpdate(entity);
        }
        getSession().flush();
    }

    @Transactional
    public void delete(Entity entity) {
        getSession().delete(entity);
    }

    public void evict(Entity entity) {
        getSession().evict(entity);
    }

    public void flush() {
        getSession().flush();
    }

    public Entity unproxy(Entity proxied) {
        Session session = getSession();
        return (Entity) ((SessionImplementor) session).getPersistenceContext().unproxy(proxied);
    }
}
