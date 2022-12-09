/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;


/**
 * Interface for resource locking.
 *
 * @author Alexander Wagner, FOCONIS AG
 *
 */
public interface ResourceLockingHandler {

  static ResourceLockingHandler instance = new ResourceLockingHandlerDb();

	/**
	 * Returns whether the locks are currently free. Can change when task is started. This method may return true, but obtaining the lock
	 * may fail.
	 */
	boolean locksObtainable(@Nonnull String taskInfo, Collection<String> readLockStrings, Collection<String> writeLockStrings);

	/**
	 * Attempts to obtain the provided read locks in the database.
	 *
	 * @return the ids of the obtained read locks, null if not obtained
	 */

	ResourceLock obtainReadLocks(@Nonnull String taskInfo, Collection<String> readLocks);

	/**
	 * Attempts to obtain the provided write locks in the database.
	 *
	 * @return true, if all write locks could be obtained, false otherwise
	 */

	ResourceLock obtainWriteLocks(@Nonnull String taskInfo, Collection<String> writeLocks) throws SQLException, Exception;

	/**
	 * Starts the resourceLockingHandler.
	 */
	void start();

	/**
	 * Stops the resourceLockingHandler gracefully. Will release all pending locks.
	 */
	void stop();

	/**
	 * The Handler-ID.
	 */
	UUID getHandlerId();

	/**
	 * Returns the bean.
	 */
	static ResourceLockingHandler getBean() {
		return instance;
	}

}
