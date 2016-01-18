package org.gagauz.hibernate.model;

import javax.persistence.*;

import java.util.Date;

@MappedSuperclass
public abstract class UpdatableModel extends Model {
    protected Date created = new Date();
    protected Date updated;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(updatable = false, columnDefinition = "timestamp on update current_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }

}
