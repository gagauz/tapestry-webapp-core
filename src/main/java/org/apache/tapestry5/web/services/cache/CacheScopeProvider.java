package org.apache.tapestry5.web.services.cache;

public interface CacheScopeProvider {

    String getScopeKey(String id, String scope);
}
