package org.gagauz.tapestry.web.services;


import org.gagauz.hibernate.model.Model;

import org.gagauz.hibernate.dao.AbstractDao;

import javax.persistence.metamodel.IdentifiableType;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;

public class CommonEntityValueEncoderFactory<E extends Model, Dao extends AbstractDao<Integer, E>> implements
        ValueEncoderFactory<E> {

    private static final String NULL = "null";

    private static boolean isNull(String arg0) {
        return null == arg0 || NULL.equals(arg0);
    }

    private final Dao dao;

    public CommonEntityValueEncoderFactory(Dao dao) {
        this.dao = dao;

    }

    @Override
    public ValueEncoder<E> create(Class<E> type) {
        return new ValueEncoder<E>() {
            @Override
            public String toClient(E arg0) {
                return null == arg0 ? NULL : String.valueOf(arg0.getId());
            }

            @Override
            public E toValue(String arg0) {
                return isNull(arg0) ? null : dao.findById(Integer.parseInt(arg0));
            }
        };
    }

}
