package com.xl0e.hibernate.types;

import java.util.Collection;
import java.util.LinkedHashSet;

public class LinkedHashSetType<E> extends CollectionType<E> {

    @Override
    public Collection<E> createCollection(Class<E> class1, int size) {
        return new LinkedHashSet<>(size);
    }
}
