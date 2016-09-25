package org.gagauz.hibernate.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.gagauz.hibernate.utils.EntityFilter;
import org.gagauz.hibernate.utils.HqlEntityFilter;
import org.gagauz.hibernate.utils.QueryParameter;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDao<Id extends Serializable, Entity> {

    private static final Map<Class<?>, Function<String, ?>> idResolverMap = new HashMap<>();

    static {
        idResolverMap.put(Byte.class, t -> {
            return null == t ? null : Byte.parseByte(t);
        });
        idResolverMap.put(Long.class, t -> {
            return null == t ? null : Long.parseLong(t);
        });
        idResolverMap.put(Integer.class, t -> {
            return null == t ? null : Integer.parseInt(t);
        });
        idResolverMap.put(String.class, t -> {
            return t;
        });
    }
    @SuppressWarnings("rawtypes")
    protected static final Map<Class, AbstractDao> instanceMap = new HashMap<Class, AbstractDao>();

    @Autowired
    protected SessionFactory sessionFactory;

    protected final Class<Id> idClass;
    protected final Class<Entity> entityClass;
    private final Function<String, Id> idDeserialiser;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        Type[] parameters = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        idClass = (Class<Id>) parameters[0];
        entityClass = (Class<Entity>) parameters[1];
        instanceMap.put(entityClass, this);
        idDeserialiser = getIdDeserializer();
    }


    @SuppressWarnings("unchecked")
    protected Function<String, Id> getIdDeserializer() {

        Function<String, ?> function = idResolverMap.get(idClass);
        if (null != function) {
            return (Function<String, Id>) function;
        }
        throw new IllegalStateException(
                "Please override getIdDeserializer() method to provide deserializer for id " + idClass);
    }

    public Id stringToId(String string) {
        return idDeserialiser.apply(string);
    }

    public String idToString(Id id) {
        return null == id ? null : id.toString();
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
        Query query = filter.createQuery(arg0 -> {
            return createQuery(arg0);
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

    public void delete(Entity entity) {
        getSession().delete(entity);
    }

    public void evict(Entity entity) {
        getSession().evict(entity);
    }

    public void flush() {
        getSession().flush();
    }

    @SuppressWarnings("unchecked")
    public Entity unproxy(Entity proxied) {
        Session session = getSession();
        return (Entity) ((SessionImplementor) session).getPersistenceContext().unproxy(proxied);
    }

    @SuppressWarnings("unchecked")
    public static <I extends Serializable, E, D extends AbstractDao<I, E>> D getDao(Class<E> entityClass) {
        return (D) instanceMap.get(entityClass);
    }

    @SuppressWarnings("rawtypes")
    public static Set<Class> getRegisteredEntities() {
        return Collections.unmodifiableSet(instanceMap.keySet());
    }

}
