/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;

/**
 * The lock type determines, where the lock is placed. A lock that is placed on <code>TENANT</code> places automatically a read lock at
 * <code>GLOBAL</code>. A <code>TABLE</code> lock places automaticalla a read lock on <code>TENANT</code> and <code>GLOBAL</code>
 *
 * @author Alexander Wagner, FOCONIS AG
 *
 */
public enum LockType {

	GLOBAL,
	TENANT,
	TABLE;

}
