package org.gagauz.hibernate.config;

public enum HibernateSysEnv {
    JDBC_USERNAME,
    JDBC_PASSWORD,
    JDBC_URL;

    private String value;

    @Override
    public String toString() {
        if (null == value) {
            value = System.getProperty(name(), System.getenv(name()));
            if (null == value) {
                throw new IllegalStateException("No system variable with name " + name() + " present!");
            }
        }
        return value;
    }
}
