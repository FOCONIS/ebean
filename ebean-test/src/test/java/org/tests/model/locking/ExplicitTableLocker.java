/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;

import io.ebean.DB;
import io.ebean.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for explicit locking of tables.
 *
 * @author Martin Trojak, FOCONIS AG
 */
public class ExplicitTableLocker implements AutoCloseable {

  static Logger log = LoggerFactory.getLogger(ExplicitTableLocker.class);

  /**
   * Creates a instance. Should be used in a autocloseable resource try block.
   *
   * @param txn
   * @param writeLocks the locks to be acquired
   */
  public static ExplicitTableLocker get(Transaction txn, String... writeLocks) {
    ExplicitTableLocker toReturn = new ExplicitTableLocker();
    switch (DB.getDefault().platform().base()) {
      case MARIADB:
      case MYSQL: {
        String locks = Stream.of(writeLocks).collect(Collectors.joining(" WRITE, ", "lock tables ", " WRITE"));
        DB.sqlUpdate(locks).execute();
        log.debug("tables locked: {} ", Arrays.toString(writeLocks));
      }
    }
    return toReturn;
  }

  /**
   * Closes the autocloseable. In this case releases the locks
   */
  @Override
  public void close() throws PersistenceException {
    switch (DB.getDefault().platform().base()) {
      case MARIADB:
      case MYSQL: {
        DB.sqlUpdate("unlock tables").execute();
        log.debug("tables unlocked");
      }
    }

  }
}
