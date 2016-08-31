package com.avaje.tests.model.softdelete;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ReferrerEntityNoSDQ {

	static final long serialVersionUID = 1L;

	@Id
	Long id;

	public Long getId() {
		return id;
	}

	@ManyToOne
	ReferencedEntityNoSDQ reference;

	String name;

	long amount;

	public ReferrerEntityNoSDQ(final ReferencedEntityNoSDQ reference, final String name, final long amount) {
		this.reference = reference;
		this.name = name;
		this.amount = amount;
	}

	public ReferencedEntityNoSDQ getReference() {
		return reference;
	}

}
