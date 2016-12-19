package org.gagauz.hibernate.model;

import java.io.Serializable;

public interface IModel<Id extends Serializable> {
    Id getId();
}
