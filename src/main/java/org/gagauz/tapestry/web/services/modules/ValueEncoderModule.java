package org.gagauz.tapestry.web.services.modules;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.gagauz.hibernate.dao.AbstractDao;

public class ValueEncoderModule {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void contributeValueEncoderSource(MappedConfiguration<Class<?>, ValueEncoderFactory<?>> configuration) {
        AbstractDao.getRegisteredEntities().forEach(cls -> {
            configuration.add(cls, new CommonEntityValueEncoderFactory(cls));
        });
    }
}
