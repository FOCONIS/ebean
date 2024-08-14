/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.basic;

import io.ebean.annotation.Cache;
import io.ebean.annotation.Formula;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.Instant;
import java.util.List;

/**
 * @author Jonas Fröhler, FOCONIS AG
 */
@Entity
@DiscriminatorValue("test_package1")
@Cache(enableBeanCache = true, enableQueryCache = true, nearCache = true)
public class TestPackage1 {

	@Id
	private Long id;

	@OneToMany(mappedBy = "testPackage", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TestPackage2> testPackage2List;

	@Formula(select = "(select max(tp2.when_created) from test_package2 tp2 where tp2.id = id)")
	private Instant currentTimeStamp;

	private String version;

	public Long getId() {
		return id;
	}

	public Instant getCurrentTimeStamp() {
		return currentTimeStamp;
	}

	public void setCurrentTimeStamp(Instant currentTimeStamp) {
		this.currentTimeStamp = currentTimeStamp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
