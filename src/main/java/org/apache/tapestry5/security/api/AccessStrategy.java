package org.apache.tapestry5.security.api;

public interface AccessStrategy {
    boolean canAccess(AccessAttributeOwner owner, AccessAttributes attribute);
}
