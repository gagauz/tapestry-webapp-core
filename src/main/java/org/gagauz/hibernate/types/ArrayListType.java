package org.gagauz.hibernate.types;


import java.util.ArrayList;
import java.util.Collection;

public class ArrayListType<E> extends CollectionType<E> {

    @Override
    public Collection<E> createCollection(Class<E> class1, int size) {
        return new ArrayList<E>(size);
    }
}
