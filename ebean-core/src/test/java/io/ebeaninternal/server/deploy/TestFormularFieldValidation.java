/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package io.ebeaninternal.server.deploy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.TestPackage1;
import org.tests.model.basic.TestPackage2;

import java.time.Instant;

/**
 * @author Jonas Fröhler, FOCONIS AG
 */
public class TestFormularFieldValidation extends BaseTest {

	@AfterEach
	public void cleanUp() {
		db.delete(TestPackage1.class);
		db.delete(TestPackage2.class);
	}

	@Test
	public void testFormulaFieldInvalidation() {
		initTables();

		TestPackage1 testPackage1 = new TestPackage1();
		testPackage1.setVersion("1");
		db.save(testPackage1);

		TestPackage2 testPackage2A = new TestPackage2();
		testPackage2A.setTestPackage(testPackage1);
		testPackage2A.setWhenCreated(Instant.parse("2024-01-01T10:00:00Z"));
		db.save(testPackage2A);

		TestPackage1 fetchedTestPackage1 = db.find(TestPackage1.class, testPackage1.getId());
		assert fetchedTestPackage1 != null;
		Assertions.assertEquals("1", fetchedTestPackage1.getVersion());
		Assertions.assertEquals(Instant.parse("2024-01-01T10:00:00Z"), fetchedTestPackage1.getCurrentTimeStamp());

		TestPackage2 testPackage2B = new TestPackage2();
		testPackage2B.setTestPackage(testPackage1);
		testPackage2B.setWhenCreated(Instant.parse("2024-01-02T10:00:00Z"));
		db.save(testPackage2B);

		TestPackage1 fetchedTestPackage2 = db.find(TestPackage1.class, testPackage1.getId());
		assert fetchedTestPackage2 != null;
		fetchedTestPackage2.setVersion("2");
		db.save(fetchedTestPackage2);

		Assertions.assertEquals("2", fetchedTestPackage2.getVersion()); // Der Assert stimmt.
		Assertions.assertEquals(Instant.parse("2024-01-02T10:00:00Z"), fetchedTestPackage2.getCurrentTimeStamp()); // Der nicht.
	}
}
