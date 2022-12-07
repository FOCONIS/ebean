/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;

/**
 * ResourceLock provided by ResourceLockingHandler.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface ResourceLock {

	/**
	 * Is this a write lock.
	 */
	boolean isWriteLock();

	/**
	 * Releases the lock.
	 */
	void release();

}
