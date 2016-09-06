package org.gagauz.tapestry.web.services;

import java.io.Serializable;
import java.util.function.Function;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.gagauz.hibernate.dao.AbstractDao;

public class CommonEntityValueEncoderFactory<I extends Serializable, E, DAO extends AbstractDao<I, E>> implements ValueEncoderFactory<E> {

    private static final Function<String, Integer> INT_RESOLVER = new Function<String, Integer>() {

        @Override
        public Integer apply(String t) {
            return Integer.parseInt(t);
        }
    };

    private static final Function<String, Long> LONG_RESOLVER = new Function<String, Long>() {

        @Override
        public Long apply(String t) {
            return Long.parseLong(t);
        }
    };

    private static final Function<String, String> STRING_RESOLVER = new Function<String, String>() {

        @Override
        public String apply(String t) {
            return t;
        }
    };

    private final DAO dao;

    public CommonEntityValueEncoderFactory(Class<E> entityClass) {
        this.dao = AbstractDao.getDao(entityClass);
    }

    @Override
    public ValueEncoder<E> create(Class<E> type) {
        return new ValueEncoder<E>() {
            @Override
            public String toClient(E arg0) {
                return dao.serializeId(dao.getIdentifier(arg0));
            }

            @Override
            public E toValue(String arg0) {
                if (null != arg0 && !"null".equalsIgnoreCase(arg0)) {
                    return dao.findById(dao.deserializeId(arg0));
                }
                return null;
            }
        };
    }

}
