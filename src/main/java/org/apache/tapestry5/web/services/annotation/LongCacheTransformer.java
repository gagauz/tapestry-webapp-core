package org.apache.tapestry5.web.services.annotation;

import java.util.List;
import java.util.Optional;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodDescription;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.apache.tapestry5.web.services.cache.CacheService;
import org.apache.tapestry5.web.services.cache.WontCacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LongCacheTransformer implements ComponentClassTransformWorker2 {

    protected static Logger LOG = LoggerFactory.getLogger(LongCacheTransformer.class);

    @Inject
    private CacheService cacheService;

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

        final String cacheKey = Optional.ofNullable(annotation.id()).map(LongCacheId::name).orElseGet(() -> method.getMethodIdentifier());

        LOG.debug("Transform method : " + cacheKey);

        return invocation -> {
            LOG.debug("Lookup in CACHE by key={}, scope={}", cacheKey, annotation.scope());

            Object value = null;
            boolean doCache = true;
            try {
                value = cacheService.getValue(cacheKey, annotation.scope());
            } catch (WontCacheException e) {
                doCache = false;
            }

            if (null != value) {
                invocation.setReturnValue(value);
                return;
            }
            invocation.proceed();
            invocation.rethrow();
            if (doCache) {
                cacheService.putValue(cacheKey, annotation.scope(), invocation.getReturnValue(),
                        annotation.unit().toMillis(annotation.value()));
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
