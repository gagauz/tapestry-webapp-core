package org.gagauz.hibernate.model.base;

import java.util.List;

public interface Parent<T> {
    List<T> getChildren();
}
