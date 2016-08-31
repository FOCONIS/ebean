package com.avaje.tests.model.softdelete;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.annotation.SoftDelete;

@Entity
public class ReferencedEntity {

	static final long serialVersionUID = 1L;

	@Id
	Long id;

	@SoftDelete
	boolean deleted;

	public Long getId() {
		return id;
	}

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
	public boolean getDeleted() {
		return deleted;
	}
}
