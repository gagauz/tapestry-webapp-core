package com.xl0e.hibernate.model.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import com.xl0e.hibernate.model.IModel;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Model implements Serializable, IModel<Integer> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Integer id;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	protected Date created = new Date();

	@Column(updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated = new Date();

	@Override
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

	@XmlTransient
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@XmlTransient
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
