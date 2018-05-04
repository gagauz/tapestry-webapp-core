package org.apache.tapestry5.web.services.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LongCache {

    long value();

    TimeUnit unit() default TimeUnit.SECONDS;

    String scope() default CacheScopeConstants.APPLICATION;

    LongCacheId id() default LongCacheId.UNSET;
}
