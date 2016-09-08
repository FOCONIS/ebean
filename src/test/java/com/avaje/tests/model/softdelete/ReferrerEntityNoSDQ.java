package com.avaje.tests.model.softdelete;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ReferrerEntityNoSDQ extends BaseSoftDelete {

	static final long serialVersionUID = 1L;

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
