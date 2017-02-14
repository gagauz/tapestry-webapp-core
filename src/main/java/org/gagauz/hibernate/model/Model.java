package org.gagauz.hibernate.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Model implements Serializable, IModel<Integer> {
    protected Integer id;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return null == id ? super.hashCode() : id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return null == id ? super.equals(obj) : id.equals(((Model) obj).getId());
    }
}
