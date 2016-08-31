package org.gagauz.utils;

public interface SysEnv<E extends Enum<E>> {
    default String getEnv(E e, String def) {
        String value = System.getenv(e.name());
        return null == value ? def : value;
    }

    default String getProperty(E e, String def) {
        return System.getProperty(e.name(), def);
    }

    default String getPropertyOrEnv(E e, String def) {
        return getProperty(e, getEnv(e, def));
    }

    default String requirePropertyOrEnv(E e) {
        String value = getProperty(e, getEnv(e, null));
        if (null != value) {
            return value;
        }
        throw new IllegalStateException("No environment variable or system property with name " + e.name() + " was defined!");
    }
}
