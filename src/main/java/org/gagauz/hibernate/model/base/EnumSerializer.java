package org.gagauz.hibernate.model.base;

public class EnumSerializer<E extends Enum<E>> implements Serializer<E> {

    @Override
    public String serialize(E object) {
        return null == object ? "null" : object.name();
    }

    @Override
    public E unserialize(String string, Class<E> clazz) {
        return "null".equals(string) ? null : Enum.valueOf(clazz, string);
    }
}
