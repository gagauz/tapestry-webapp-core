package org.gagauz.tapestry.web.services.modules;

import java.io.Serializable;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.gagauz.hibernate.dao.AbstractDao;

public class CommonEntityValueEncoderFactory<I extends Serializable, E, DAO extends AbstractDao<I, E>> implements ValueEncoderFactory<E> {

    private final DAO dao;

    public CommonEntityValueEncoderFactory(Class<E> entityClass) {
        this.dao = AbstractDao.getDao(entityClass);
        if (null == dao) {
            throw new IllegalStateException("No dao was found for entity class " + entityClass);
        }
    }

    @Override
    public ValueEncoder<E> create(Class<E> type) {
        return new ValueEncoder<E>() {
            @Override
            public String toClient(E arg0) {
                return null == arg0 ? null : dao.idToString(dao.getIdentifier(arg0));
            }

            @Override
            public E toValue(String arg0) {
                if (null != arg0 && !"null".equalsIgnoreCase(arg0)) {
                    return dao.findById(dao.stringToId(arg0));
                }
                return null;
            }
        };
    }

}
