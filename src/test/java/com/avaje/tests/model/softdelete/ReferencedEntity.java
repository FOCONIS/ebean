package com.avaje.tests.model.softdelete;

import javax.persistence.Entity;

@Entity
public class ReferencedEntity extends BaseSoftDelete {

	static final long serialVersionUID = 1L;

	String name;

	String description;

	public ReferencedEntity(final String name, final String description) {
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
