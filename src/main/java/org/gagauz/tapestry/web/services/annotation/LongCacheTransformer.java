package org.gagauz.tapestry.web.services.annotation;


import org.apache.tapestry5.internal.transform.MethodResultCache;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.*;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.gagauz.util.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LongCacheTransformer implements ComponentClassTransformWorker2 {

    protected static Logger LOG = LoggerFactory.getLogger(LongCacheTransformer.class);

    private static final Map<String, CachedValue> CACHE = C.newHashMap();

    private class CachedValue implements MethodResultCache {
        private final long expireAfterMillis;
        private long lastSetMillis;
        private Object cachedValue;

        public CachedValue(LongCache annotation) {
            expireAfterMillis = TimeUnit.MILLISECONDS.convert(annotation.value(), annotation.unit());
            LOG.debug("Create new cache value holder, expires after (ms) : " + expireAfterMillis);
        }

        @Override
        public void set(Object cachedValue) {
            this.lastSetMillis = System.currentTimeMillis();
            this.cachedValue = cachedValue;
        }

        @Override
        public Object get() {
            return cachedValue;
        }

        @Override
        public boolean isCached() {
            return null != cachedValue && (lastSetMillis + expireAfterMillis > System.currentTimeMillis());
        }

        @Override
        public void reset() {
            lastSetMillis = 0;
            cachedValue = null;
        }
    }

    @Override
    public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model) {
        List<PlasticMethod> methods = plasticClass.getMethodsWithAnnotation(LongCache.class);
        for (PlasticMethod method : methods) {
            validateMethod(method);
            adviseMethod(plasticClass, method);
        }
    }

    private void adviseMethod(PlasticClass plasticClass, PlasticMethod method) {
        MethodAdvice advice = createAdvice(plasticClass, method);
        method.addAdvice(advice);
    }

    private MethodAdvice createAdvice(PlasticClass plasticClass, PlasticMethod method) {
        final LongCache annotation = method.getAnnotation(LongCache.class);

        final String cacheKey = plasticClass.getClassName() + "." + method.getDescription().methodName;

        LOG.debug("Transform method : " + cacheKey);

        CACHE.put(cacheKey, new CachedValue(annotation));

        return new MethodAdvice() {
            @Override
            public void advise(MethodInvocation invocation) {
                LOG.debug("Lookup in CACHE " + cacheKey);
                CachedValue cacheValue = CACHE.get(cacheKey);
                LOG.debug("Cache holder is  " + cacheValue);
                if (cacheValue.isCached()) {
                    LOG.debug("Cached value is still valid, return it.");
                    invocation.setReturnValue(cacheValue.get());
                    return;
                }
                LOG.debug("Cached value is not valid, return refresh it.");
                invocation.proceed();
                invocation.rethrow();
                cacheValue.set(invocation.getReturnValue());
            }
        };
    }

    private void validateMethod(PlasticMethod method) {
        MethodDescription description = method.getDescription();

        if (description.returnType.equals("void"))
            throw new IllegalArgumentException(String.format(
                    "Method %s may not be used with @Cached because it returns void.", method.getMethodIdentifier()));

        if (description.argumentTypes.length != 0)
            throw new IllegalArgumentException(String.format(
                    "Method %s may not be used with @Cached because it has parameters.", method.getMethodIdentifier()));
    }
}
