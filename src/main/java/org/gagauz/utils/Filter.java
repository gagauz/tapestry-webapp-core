package org.gagauz.utils;

public interface Filter<E> {
    boolean apply(E element);
}
