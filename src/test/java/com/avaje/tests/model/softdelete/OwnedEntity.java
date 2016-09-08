package com.avaje.tests.model.softdelete;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class OwnedEntity extends BaseSoftDelete {

	static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private OwnerEntity parent;

	private String name;

	public OwnedEntity(final String name) {
		this.name = name;
	}

	public OwnerEntity getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}

}
