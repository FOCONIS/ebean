/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.basic;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.Instant;

/**
 * @author Jonas Fröhler, FOCONIS AG
 */
@Entity
@DiscriminatorValue("test_package2")
public class TestPackage2 {

	@Id
	private Long id;

	Instant whenCreated;

	@ManyToOne
	private TestPackage1 testPackage;

	public Instant getWhenCreated() {
		return whenCreated;
	}

	public void setWhenCreated(Instant whenCreated) {
		this.whenCreated = whenCreated;
	}

	public TestPackage1 getTestPackage() {
		return testPackage;
	}

	public void setTestPackage(TestPackage1 testPackage1) {
		this.testPackage = testPackage1;
	}
}
