package org.apache.tapestry5.web.services.security;

import java.util.Arrays;
import java.util.Collection;

import org.apache.tapestry5.security.api.AccessAttributes;

public class SecuredAccessAttributes implements AccessAttributes {

    private final Collection<String> attributes;

    public SecuredAccessAttributes(String... value) {
        this.attributes = Arrays.asList(value);
    }

    public SecuredAccessAttributes(Collection<String> value) {
        this.attributes = value;
    }

    public Collection<String> getAttributes() {
        return attributes;
    }

}
