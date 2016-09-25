package org.gagauz.tapestry.web.services.modules;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.TypeCoercer;

import java.util.*;

public class TypeCoercerModule {

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

    }
}
