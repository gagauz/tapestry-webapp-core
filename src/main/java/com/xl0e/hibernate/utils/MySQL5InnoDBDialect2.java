package com.xl0e.hibernate.utils;

import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;

public class MySQL5InnoDBDialect2 extends MySQL5InnoDBDialect {
    public MySQL5InnoDBDialect2() {
        super();
        registerFunction("bitwise_and", new SQLFunctionTemplate(IntegerType.INSTANCE, "(?1 & ?2)"));
        registerFunction("bitwise_or", new SQLFunctionTemplate(IntegerType.INSTANCE, "(?1 | ?2)"));
    }

}
