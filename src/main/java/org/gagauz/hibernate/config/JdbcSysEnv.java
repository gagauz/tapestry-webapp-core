package org.gagauz.hibernate.config;

import org.gagauz.utils.SysEnv;

public enum JdbcSysEnv implements SysEnv<JdbcSysEnv> {
    JDBC_USERNAME,
    JDBC_PASSWORD,
    JDBC_URL,
    JDBC_DRIVER;

    private String value;

    @Override
    public String toString() {
        if (null == value) {
            value = requirePropertyOrEnv(this);
        }
        return value;
    }
}
