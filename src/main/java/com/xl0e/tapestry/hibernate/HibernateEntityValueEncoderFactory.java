package com.xl0e.tapestry.hibernate;

import java.io.Serializable;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.hibernate.TransientObjectException;

import com.xl0e.hibernate.dao.AbstractHibernateDao;
import com.xl0e.hibernate.model.IModel;
import com.xl0e.util.StringUtils;

public class HibernateEntityValueEncoderFactory<I extends Serializable, E extends IModel<I>, DAO extends AbstractHibernateDao<I, E>>
        implements ValueEncoderFactory<E>, Coercion<String, E> {

    private final DAO dao;

    public HibernateEntityValueEncoderFactory(Class<E> entityClass) {
        this.dao = AbstractHibernateDao.getDao(entityClass);
        if (null == this.dao) {
            throw new IllegalStateException("No DAO was found for entity class " + entityClass);
        }
    }

    private E stringToEntity(String string) {
        if (!StringUtils.isEmpty(string) && !"null".equalsIgnoreCase(string)) {
            E e = HibernateEntityValueEncoderFactory.this.dao.findById(HibernateEntityValueEncoderFactory.this.dao.stringToId(string));
            return e;
        }
        return null;
    }

    @Override
    public ValueEncoder<E> create(Class<E> type) {
        return new ValueEncoder<E>() {
            @Override
            public String toClient(E arg0) {
                try {
                    return null == arg0
                            ? null
                            : HibernateEntityValueEncoderFactory.this.dao.idToString(arg0.getId());
                } catch (TransientObjectException e) {
                    return null;
                }
            }

            @Override
            public E toValue(String arg0) {
                return stringToEntity(arg0);

            }
        };
    }

    @Override
    public E coerce(String input) {
        return stringToEntity(input);
    }
}
