package com.avaje.tests.model.softdelete;

import javax.persistence.Entity;

import com.avaje.ebean.annotation.SoftDeleteAlwaysFetch;

@Entity
@SoftDeleteAlwaysFetch
public class ReferencedEntityNoSDQ extends BaseSoftDelete {

	static final long serialVersionUID = 1L;

	String name;

	String description;

	public ReferencedEntityNoSDQ(final String name, final String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
