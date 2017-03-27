package com.xl0e.hibernate.model.base;

import java.util.List;

public interface Parent<T> {
    T getParent();

    List<T> getChildren();

}
