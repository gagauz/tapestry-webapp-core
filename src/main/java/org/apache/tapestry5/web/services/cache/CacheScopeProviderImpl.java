package org.apache.tapestry5.web.services.cache;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class CacheScopeProviderImpl implements CacheScopeProvider {

    @Inject
    private RequestGlobals requestGlobals;

    @Override
    public String getScopeKey(String id, String scope) {
        switch (scope) {
        case "APPLICATION": {
            return "";
        }
        case "SESSION": {
            return Optional.ofNullable(requestGlobals.getHTTPServletRequest())
                    .map(HttpServletRequest::getSession)
                    .map(HttpSession::getId)
                    .orElse(null); // Do not cache
        }
        default: {
            return null; // Do not cache
        }
        }

    }

}
