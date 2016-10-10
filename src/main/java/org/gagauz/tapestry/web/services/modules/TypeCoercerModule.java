package org.gagauz.tapestry.web.services.modules;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.gagauz.hibernate.dao.AbstractDao;

public class TypeCoercerModule {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Contribute(TypeCoercer.class)
    public static void contributeTypeCoercer(Configuration<CoercionTuple> configuration) {
        configuration.add(CoercionTuple.create(List.class, Set.class, new Coercion<List, Set>() {
            @Override
            public Set coerce(List input) {
                return new HashSet(input);
            }
        }));

        configuration.add(CoercionTuple.create(Collection.class, EnumSet.class, new Coercion<Collection, EnumSet>() {

            @Override
            public EnumSet coerce(Collection input) {

                return null == input || input.isEmpty() ? null : EnumSet.copyOf(input);
            }
        }));


        AbstractDao.getRegisteredEntities().forEach(cls -> {
            configuration.add(CoercionTuple.create(String.class, cls, new CommonEntityValueEncoderFactory(cls)));
        });
    }
}
