package org.gagauz.hibernate.model.base;

public class IntegerSerializer implements Serializer<Integer> {

    @Override
    public String serialize(Integer object) {
        return String.valueOf(object);
    }

    @Override
    public Integer unserialize(String string, Class<Integer> clazz) {
        return "null".equals(string) ? null : Integer.parseInt(string);
    }
}
