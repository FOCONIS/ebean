package com.avaje.tests.model.softdelete;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ReferrerEntity extends BaseSoftDelete {

	static final long serialVersionUID = 1L;

	@ManyToOne
	ReferencedEntity reference;

	String name;

	long amount;

	public ReferrerEntity(final ReferencedEntity reference, final String name, final long amount) {
		this.reference = reference;
		this.name = name;
		this.amount = amount;
	}

	public ReferencedEntity getReference() {
		return reference;
	}

}
