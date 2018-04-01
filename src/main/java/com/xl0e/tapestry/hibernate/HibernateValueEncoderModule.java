package com.xl0e.tapestry.hibernate;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.services.ValueEncoderFactory;

import com.xl0e.hibernate.dao.AbstractHibernateDao;

public class HibernateValueEncoderModule {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void contributeValueEncoderSource(MappedConfiguration<Class<?>, ValueEncoderFactory<?>> configuration) {
        AbstractHibernateDao.getRegisteredEntities().forEach(cls -> {
            configuration.add(cls, new HibernateEntityValueEncoderFactory(cls));
        });
    }
}
