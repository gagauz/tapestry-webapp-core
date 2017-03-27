package com.xl0e.hibernate.model.base;

public class StringSerializer implements Serializer<String> {

    @Override
    public String serialize(String object) {
        return null == object ? "null" : object;
    }

    @Override
    public String unserialize(String string, Class<String> clazz) {
        return "null".equals(string) ? null : string;
    }
}
