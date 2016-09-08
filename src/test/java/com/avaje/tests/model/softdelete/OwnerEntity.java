package com.avaje.tests.model.softdelete;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class OwnerEntity extends BaseSoftDelete {

	static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<OwnedEntity> children;

	private String name;

	public OwnerEntity(final String name) {
		this.name = name;
	}

	public List<OwnedEntity> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public void addChild(OwnedEntity child) {
		children.add(child);
	}
}
