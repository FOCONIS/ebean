/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Lock class.
 *
 * @author Alexander Wagner, FOCONIS AG
 */
public class Lock implements Comparable<Lock> {

  public Lock(@Nonnull LockType type, @Nonnull String name) {
    this.type = type;
    this.name = name;
  }

  public static final @Nonnull String GLOBAL_NAME = "_global";
  public static final @Nonnull String TENANT_NAME = "_tenant";

  private static final @Nonnull Lock GLOBAL = new Lock(LockType.GLOBAL, GLOBAL_NAME);
  private static final @Nonnull Lock TENANT = new Lock(LockType.TENANT, TENANT_NAME);

  public static @Nonnull Collection<Lock> of(final Lock... locks) {
    return Collections.unmodifiableCollection(Arrays.asList(locks));
  }

  /**
   * Returns an empty Collection of Locks.
   */
  public static @Nonnull Collection<Lock> ofNone() {
    return Collections.emptyList();
  }

  /**
   * Returns a Collection of the global Lock.
   */
  public static @Nonnull Collection<Lock> ofGlobal() {
    return of(global());
  }

  /**
   * Returns a Collection of the current tenant Lock.
   */
  public static @Nonnull Collection<Lock> ofTenant() {
    return of(tenant());
  }

  /**
   * The global lock. Placing this lock as write lock means, that this task runs exclusively in this cluster. No other task may run
   */
  public static @Nonnull Lock global() {
    return GLOBAL;
  }

  /**
   * The tenant lock. Placing this lock as write lock means, that this task runs exclusively on this tenant. Other tasks in other tenants
   * may run. Locking the tenant will also place a global read lock.
   */
  public static @Nonnull Lock tenant() {
    return TENANT;
  }

  /**
   * The table lock. Placing a lock on one (or more) tables means, that this task may run exclusively on these tables (in that tenant).
   * Locking a table will also place a tenant and global read lock.
   */
  public static @Nonnull Lock table(final @Nonnull String tableName) {
    return new Lock(LockType.TABLE, tableName);
  }

  private final @Nonnull LockType type;

  private final @Nonnull String name;

  @Override
  public int compareTo(final Lock o) {
    return Comparator.comparing(Lock::getType).thenComparing(Lock::getName).compare(this, o);
  }

  @Nonnull
  public LockType getType() {
    return type;
  }

  @Nonnull
  public String getName() {
    return name;
  }

}
