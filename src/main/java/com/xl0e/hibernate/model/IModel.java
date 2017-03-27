package com.xl0e.hibernate.model;

import java.io.Serializable;

public interface IModel<Id extends Serializable> {
    Id getId();
}
