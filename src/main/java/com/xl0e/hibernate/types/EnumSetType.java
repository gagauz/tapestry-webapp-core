package com.xl0e.hibernate.types;


import java.util.Collection;
import java.util.EnumSet;

public class EnumSetType<E extends Enum<E>> extends CollectionType<E> {

    @Override
    public Collection<E> createCollection(Class<E> class1, int size) {
        return EnumSet.noneOf(class1);
    }
}
