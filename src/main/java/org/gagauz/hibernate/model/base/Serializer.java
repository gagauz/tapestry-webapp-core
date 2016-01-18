package org.gagauz.hibernate.model.base;

public interface Serializer<P> {
    String serialize(P object);

    P unserialize(String string, Class<P> clazz);
}
