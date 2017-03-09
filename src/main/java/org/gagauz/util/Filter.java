package org.gagauz.util;

public interface Filter<E> {
    boolean apply(E element);
}
