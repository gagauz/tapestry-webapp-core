package org.gagauz.hibernate.model.base;

import java.util.List;

public interface Parent<T> {
    T getParent();

    List<T> getChildren();

}
