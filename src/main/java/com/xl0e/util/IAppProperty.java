package com.xl0e.util;

public interface IAppProperty<E extends Enum<E>> {

    String getDefaultValue();

    @SuppressWarnings("unchecked")
    default String getName() {
        return getPrefix() + ((E) this).name();
    }

    default String getEnv() {
        String value = System.getenv(getName());
        return null == value ? getDefaultValue() : value;
    }

    default String getPrefix() {
        return "";
    }

    default String getProperty() {
        return System.getProperty(getName(), getDefaultValue());
    }

    default String getString() {
        return getProperty();
    }

    default Integer getInteger() {
        return Integer.parseInt(getProperty());
    }

    default Boolean getBoolean() {
        return Boolean.parseBoolean(getProperty());
    }
}
