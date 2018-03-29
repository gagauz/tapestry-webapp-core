package org.apache.tapestry5.web.services.security;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.security.api.AccessAttribute;

public class SecuredAccessAttributes implements AccessAttribute {

    private final List<String> attributes;

    public SecuredAccessAttributes(String[] value) {
        this.attributes = Arrays.asList(value);
    }

    public List<String> getAttributes() {
        return attributes;
    }

}
