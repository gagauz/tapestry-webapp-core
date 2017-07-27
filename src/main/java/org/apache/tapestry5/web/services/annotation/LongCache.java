package org.apache.tapestry5.web.services.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LongCache {

    long value();

    TimeUnit unit() default TimeUnit.SECONDS;
}
