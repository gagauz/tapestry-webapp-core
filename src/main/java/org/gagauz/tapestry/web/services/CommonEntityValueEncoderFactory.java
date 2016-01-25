package org.gagauz.tapestry.web.services;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.gagauz.hibernate.dao.AbstractDao;
import org.gagauz.hibernate.model.Model;

import java.io.Serializable;

public class CommonEntityValueEncoderFactory<E extends Model, Dao extends AbstractDao<Serializable, E>> implements ValueEncoderFactory<E> {

    private final Dao dao;

    public CommonEntityValueEncoderFactory(Dao dao) {
        this.dao = dao;
    }

    @Override
    public ValueEncoder<E> create(Class<E> type) {
        return new ValueEncoder<E>() {
            @Override
            public String toClient(E arg0) {
                return null == arg0 ? null : String.valueOf(arg0.getId());
            }

            @Override
            public E toValue(String arg0) {
                if (null != arg0 && !"null".equalsIgnoreCase(arg0)) {
                    if (dao.idClass.equals(Integer.class)) {
                        Serializable id = Integer.parseInt(arg0);
                        return dao.findById(id);
                    } else if (dao.idClass.equals(Long.class)) {
                        Serializable id = Long.parseLong(arg0);
                        return dao.findById(id);
                    } else if (dao.idClass.equals(String.class)) {
                        return dao.findById(arg0);
                    }
                }
                return null;
            }
        };
    }

}
