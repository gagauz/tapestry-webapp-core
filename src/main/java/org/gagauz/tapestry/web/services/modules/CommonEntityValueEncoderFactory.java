package org.gagauz.tapestry.web.services.modules;

import java.io.Serializable;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.gagauz.hibernate.dao.AbstractDao;
import org.gagauz.utils.StringUtils;

public class CommonEntityValueEncoderFactory<I extends Serializable, E, DAO extends AbstractDao<I, E>>
implements ValueEncoderFactory<E>, Coercion<String, E> {

    private final DAO dao;

    public CommonEntityValueEncoderFactory(Class<E> entityClass) {
        this.dao = AbstractDao.getDao(entityClass);
        if (null == this.dao) {
            throw new IllegalStateException("No DAO was found for entity class " + entityClass);
        }
    }

    private E stringToEntity(String string) {
        if (!StringUtils.isEmpty(string) && !"null".equalsIgnoreCase(string)) {
            E e = CommonEntityValueEncoderFactory.this.dao.findById(CommonEntityValueEncoderFactory.this.dao.stringToId(string));
            return e;
        }
        return null;
    }

    @Override
    public ValueEncoder<E> create(Class<E> type) {
        return new ValueEncoder<E>() {
            @Override
            public String toClient(E arg0) {

                String s = null == arg0
                        ? null
                                : CommonEntityValueEncoderFactory.this.dao.idToString(CommonEntityValueEncoderFactory.this.dao.getIdentifier(arg0));
                return s;
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
