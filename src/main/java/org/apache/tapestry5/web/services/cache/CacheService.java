package org.apache.tapestry5.web.services.cache;

public interface CacheService {

    <T> T getValue(String id, String scope) throws WontCacheException;

    <T> void putValue(String id, String scope, T value, long timeout);

}
