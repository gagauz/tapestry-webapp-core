package org.apache.tapestry5.web.services.cache;

import java.util.Map;
import java.util.Optional;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.xl0e.util.C;

public class CacheServiceImpl implements CacheService {

    private static final Map<String, CachedValue<?>> CACHE = C.newHashMap();

    @Inject
    private CacheScopeProvider cacheScopeProvider;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(String key, String scope) throws WontCacheException {
        final String cacheKey = computeKey(key, scope);
        if (null == cacheKey) {
            throw new WontCacheException();
        }
        return (T) Optional.ofNullable(CACHE.get(cacheKey))
                .filter(CachedValue::isAlive)
                .map(CachedValue::get)
                .orElse(null);
    }

    @Override
    public <T> void putValue(String key, String scope, T value, long timeout) {
        CACHE.put(computeKey(key, scope), new CachedValue<>(value, timeout + System.currentTimeMillis()));
    }

    protected String computeKey(String key, String scope) {
        String scopeId = cacheScopeProvider.getScopeKey(key, scope);
        if (null == scopeId) {
            return null;
        }

        return scopeId + '.' + key;
    }

    static class CachedValue<T> {
        private final long expires;
        private final T value;

        public CachedValue(T value, long expires) {
            this.value = value;
            this.expires = expires;

        }

        public T get() {
            return value;
        }

        public boolean isAlive() {
            return System.currentTimeMillis() < expires;
        }

    }

}
