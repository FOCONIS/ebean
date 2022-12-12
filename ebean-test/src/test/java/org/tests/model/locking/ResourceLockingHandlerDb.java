/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;

import io.ebean.DB;
import io.ebean.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Nonnull;
import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that handles resource locks.
 *
 * @author Alexander Wagner, FOCONIS AG
 */
public class ResourceLockingHandlerDb implements ResourceLockingHandler {

  Logger log = LoggerFactory.getLogger(ResourceLockingHandlerDb.class);

  private class ResourceLockDb implements ResourceLock {

    private final boolean writeLock;
    private final List<UUID> lockIds;
    private final String taskInfo;
    private final Collection<String> lockNames;

    public ResourceLockDb(boolean writeLock, List<UUID> lockIds, String taskInfo, Collection<String> lockNames) {
      this.writeLock = writeLock;
      this.lockIds = lockIds;
      this.taskInfo = taskInfo;
      this.lockNames = lockNames;
    }

    @Override
    public boolean isWriteLock() {
      return writeLock;
    }

    public List<UUID> getLockIds() {
      return lockIds;
    }

    public String getTaskInfo() {
      return taskInfo;
    }

    public Collection<String> getLockNames() {
      return lockNames;
    }

    @Override
    public void release() {
      boolean lockReleased = false;
      try {
        if (isWriteLock()) {
          lockReleased = releaseWriteLocks(lockIds, taskInfo, lockNames);
        } else {
          lockReleased = releaseReadLocks(lockIds, taskInfo, lockNames);
        }
      } finally {
        if (lockReleased) {
          locksOutMap.remove(this);
        } else {
          locksOutMap.put(this, Boolean.FALSE);
        }
      }
    }
  }

  private enum State {
    STOPPED, SHUTTING_DOWN, STARTED;
  }


  private UUID handlerId = UUID.randomUUID();

  @Override
  public UUID getHandlerId() {
    return handlerId;
  }

  private PeriodicThread heartbeatThread;

  private final Clock clock;

  private volatile Map<ResourceLockDb, Boolean> locksOutMap = new ConcurrentHashMap<>();

  private int heartbeatCount;

  private State running = State.STOPPED;

  /**
   * Component Constructor.
   */
  public ResourceLockingHandlerDb() {
    this(Clock.systemDefaultZone());
  }

  /**
   * Constructor for testing. Does not start the heartbeat thread.
   */
  public ResourceLockingHandlerDb(final Clock clock) {
    this.clock = clock;
  }

  @Override
  public synchronized void start() {
    if (running == State.STOPPED) {
      log.info("Starting ResourceLockingHandler with id = {}", handlerId);
      heartbeatThread = new PeriodicThread(this::heartbeat, "resourcelock-heartbeat", 30000);
      heartbeatThread.start();
      running = State.STARTED;
    }
  }

  @Override
  public synchronized void stop() {
    try {

      if (running == State.STARTED) {
        running = State.SHUTTING_DOWN;
      } else {
        return;
      }

      //check if there are open logs on shutdown from this handler. This is probably a programming error.
      List<ReadWriteLock> staleLocks = DB.find(ReadWriteLock.class).where().eq("locked", handlerId).findList();
      resetStaleLocks(staleLocks);
      if (heartbeatThread != null) {
        heartbeatThread.shutdown(5000);
        heartbeatThread = null;
      }
      running = State.STOPPED;

    } catch (Throwable th) {

    }
  }

  /**
   * Resets unfinished or stale read-locks and locked write-locks. Do not pass unlocked write-locks, as they are not deleted and needed
   * for read-locks as parent.
   */
  private void resetStaleLocks(final List<ReadWriteLock> staleLocks) {
    for (ReadWriteLock staleLock : staleLocks) {
      if (handlerId.equals(staleLock.getLocked())) {
        // If we go here, these are possible programming errors
        if (staleLock.getName() == null) {
          log.error("Found stale read-lock for '{}' of task '{}' from this handler",
            staleLock.getResourceName(),
            staleLock.getTaskInfo());
        } else {
          log.error("Found stale write-lock for '{}' of task '{}' from this handler",
            staleLock.getName(),
            staleLock.getTaskInfo());
        }
      } else {
        // otherwise we clean up the mess of other/former tomcat threads
        if (staleLock.getName() == null) {
          if (staleLock.getLocked() == null) {
            log.warn("Found unfinished read-lock for '{}' of task '{}'",
              staleLock.getResourceName(),
              staleLock.getTaskInfo());

          } else {
            log.warn("Found stale read-lock for '{}' of task '{}' from handler {}",
              staleLock.getResourceName(),
              staleLock.getTaskInfo(),
              staleLock.getLocked());
          }
        } else {
          log.warn("Found stale write-lock for '{}' of task '{}' from handler {}",
            staleLock.getName(),
            staleLock.getTaskInfo(),
            staleLock.getLocked());
        }
      }
      if (staleLock.getName() == null) {
        DB.delete(staleLock); // it was a read lock
      } else {
        staleLock.setLocked(null);
        staleLock.setTaskInfo(null);
        staleLock.setLockTime(clock.instant());
        DB.save(staleLock);
      }
    }

    Iterator<Entry<ResourceLockDb, Boolean>> iter = locksOutMap.entrySet().iterator();
    while (iter.hasNext()) {
      Entry<ResourceLockDb, Boolean> entry = iter.next();
      if (Boolean.FALSE.equals(entry.getValue())) {
        ResourceLockDb elem = entry.getKey();
        try {
          if (elem.isWriteLock()) {
            log.warn("Found stale write-locks for '{}' of task '{}'", elem.lockNames, elem.taskInfo);
            DB.sqlUpdate(
                "update read_write_lock set locked = null, task_info = null, lock_time = :lockTime where id in (:ids)")
              .setParameter("lockTime", clock.instant())
              .setParameter("ids", elem.lockIds)
              .executeNow();
          } else {
            log.warn("Found stale read-locks for '{}' of task '{}'", elem.lockNames, elem.taskInfo);
            DB.find(ReadWriteLock.class).where().in("id", elem.lockIds).delete();
          }

          iter.remove();
        } catch (PersistenceException e) {
          log.error("could not release stale read-write-locks for '{}' of task '{}'", elem.lockNames, elem.taskInfo, e);
        }
      }
    }
  }

  /**
   * Returns whether the locks are currently free. Can change when task is started.
   */
  @Override
  public boolean locksObtainable(final String taskInfo, final Collection<String> readLocks, final Collection<String> writeLocks) {
    checkContext();
    boolean ret;
    try {
      if (readLocks.isEmpty() && writeLocks.isEmpty()) {
        ret = true;
      } else if (readLocks.isEmpty()) {
        // @formatter:off
        ret = !DB.find(ReadWriteLock.class).where()
          .or()
          .in("name", writeLocks)
          .in("parent.name", writeLocks)
          .endOr()
          .isNotNull("locked")
          .setUseQueryCache(true)
          .exists();
        // @formatter:on
      } else if (writeLocks.isEmpty()) {
        // @formatter:off
        ret = !DB.find(ReadWriteLock.class).where()
          .in("name", readLocks)
          .isNotNull("locked")
          .setUseQueryCache(true)
          .exists();
        // @formatter:on
      } else {
        // @formatter:off
        ret = !DB.find(ReadWriteLock.class).where()
          .or()
          .in("name", readLocks)
          .in("name", writeLocks)
          .in("parent.name", writeLocks)
          .endOr()
          .isNotNull("locked")
          .setUseQueryCache(true)
          .exists();
        // @formatter:on
      }
    } catch (PersistenceException e) {
      log.error("censureWriteLockExists failed {}", "hmm", e);
      ret = false;
    }

    if (ret) {
      log.debug("read-locks '{}' and write-locks '{}' are obtainable for task '{}'", readLocks, writeLocks, taskInfo);
    } else {
      log.debug("read-locks '{}' and write-locks '{}' are not obtainable for task '{}'", readLocks, writeLocks, taskInfo);
    }
    return ret;
  }

  /**
   * Attempts to acquire the provided read locks in the database.
   *
   * @return true, if all read locks could be obtained, false otherwise
   */
  @Override
  public ResourceLock obtainReadLocks(final String taskInfo, final Collection<String> readLocks) {

    String taskInfoShort = left(taskInfo, 255);

    checkContext();

    List<UUID> ret = new ArrayList<>();
    if (!readLocks.isEmpty()) {
      List<UUID> toResetIds = new ArrayList<>();
      List<String> toResetNames = new ArrayList<>();

      for (String readLock : readLocks) {
        // WriteLock holen/erzeugen
        ReadWriteLock parent = ensureWriteLockExists(readLock);
        if (parent.getLocked() != null) {
          // writeLock gelocked -> es kann kein ReadLock mehr geben.
          ret = null;
          break;
        }
        try {
          ReadWriteLock readWriteLock = new ReadWriteLock()
            .setTaskInfo(taskInfoShort)
            .setParent(parent)
            .setLockTime(clock.instant());
          DB.save(readWriteLock); // throws dataIntegrityException
          toResetIds.add(readWriteLock.getId());
          toResetNames.add(readLock);

          int picked = 0;
          try {
            // raw weil Ebean für MariaDB die subquery nicht richtig berechnet
            picked = DB.sqlUpdate("update read_write_lock set locked = :handlerId, task_info = :taskInfo "
                + "where id in (select sq.id from "
                + "(select rl.id from read_write_lock rl "
                + "left join read_write_lock wl on wl.id = rl.parent_id "
                + "where rl.id = :id and wl.locked is null) sq)")
              .setParameter("handlerId", handlerId)
              .setParameter("taskInfo", taskInfoShort)
              .setParameter("id", readWriteLock.getId())
              .executeNow();
          } catch (PersistenceException e) {
            // NOP - ging halt nicht
            log.error("could not obtain readlock {}", readLock, e);
          }

          if (picked == 1) {
            ret.add(readWriteLock.getId());
          } else {
            ret = null;
            break;
          }
        } catch (PersistenceException e) {
          log.error("could not obtain readlock {}", readLock, e);
          // lock already exists
          ret = null;
          break;
        }
      }

      if (ret == null) {
        // Mögliche schon erhaltene read-locks wieder hergeben, falls wir nicht alle bekommen
        releaseReadLocks(toResetIds, taskInfo, toResetNames);
      }
    }
    if (ret != null) {
      log.debug("read-locks '{}' obtained for task '{}'", readLocks, taskInfo);
      ResourceLockDb lock = new ResourceLockDb(false, ret, taskInfo, readLocks);
      locksOutMap.put(lock, Boolean.TRUE);
      return lock;
    } else {
      log.debug("read-locks '{}' not obtained for task '{}'", readLocks, taskInfo);
      return null;
    }
  }

  /**
   * Attempts to acquire the provided write locks in the database.
   *
   * @return true, if all write locks could be obtained, false otherwise
   */
  @Override
  public ResourceLock obtainWriteLocks(final String taskInfo, final Collection<String> writeLocks) throws Exception {
    assert taskInfo != null;
    String taskInfoShort = left(taskInfo, 255);

    checkContext();

    List<UUID> ret = new ArrayList<>();
    if (!writeLocks.isEmpty()) {
      List<UUID> toResetIds = new ArrayList<>();
      List<String> toResetNames = new ArrayList<>();
      for (String writeLock : writeLocks) {
        ReadWriteLock parent = ensureWriteLockExists(writeLock);
        if (parent.getLocked() != null) {
          ret = null;
          break; // already locked -> fertig
        }
        int picked = 0;
        Connection connection0 = DB.getDefault().dataSource().getConnection();
        try (Transaction txn = DB.beginTransaction(); ExplicitTableLocker tableLock = ExplicitTableLocker.get(null,
          "read_write_lock", "read_write_lock rl")) {
          // raw weil Ebean für MariaDB die subquery nicht richtig berechnet
          picked = DB.sqlUpdate("update read_write_lock "
              + "set locked = :handlerId, task_info= :taskInfo "
              + "where id = :parentId and locked is null and "
              + "id not in (select parent_id from read_write_lock rl where rl.locked is not null and rl.parent_id is not null)")
            .setParameter("handlerId", handlerId)
            .setParameter("taskInfo", taskInfoShort)
            .setParameter("parentId", parent.getId())
            .executeNow();
          txn.commit();
        }
        if (picked == 1) {
          ret.add(parent.getId());
          toResetIds.add(parent.getId());
          toResetNames.add(writeLock);
        } else {
          ret = null;
          break;
        }
      }

      if (ret == null) {
        // Mögliche schon erhaltene write-locks wieder hergeben, falls wir nicht alle bekommen
        releaseWriteLocks(toResetIds, taskInfo, toResetNames);
      }
    }

    if (ret != null) {
      log.debug("write-locks '{}' obtained for task '{}'", writeLocks, taskInfo);
      ResourceLockDb lock = new ResourceLockDb(true, ret, taskInfo, writeLocks);
      locksOutMap.put(lock, Boolean.TRUE);
      return lock;
    } else {
      log.debug("write-locks '{}' not obtained for task '{}'", writeLocks, taskInfo);
      return null;
    }
  }

  /**
   * Removes the provided read locks from the database.
   */
  private boolean releaseReadLocks(final Collection<UUID> readLocks, final String taskInfo, final Collection<String> lockNames) {
    checkContext();
    int count = 0;
    if (!readLocks.isEmpty()) {
      /**try (Transaction txn = DB.beginTransaction()) {
       // below select here because of a deadlock, first lock the parent index and then pripmary index with the delete call below
       DB.sqlQuery("select count(id) from read_write_lock where parent_id in (:parentId) and locked is not null").setParameter(
       "parentId", readLocks).findOne();

       count = DB.find(ReadWriteLock.class).where().in("id", readLocks).delete();
       txn.commit();
       } catch (PersistenceException e) {
       log.error("read-locks '{}' release for task '{}' failed: Could not release any lock.", lockNames, taskInfo, e);
       return false;
       }**/


      count = DB.find(ReadWriteLock.class).where().in("id", readLocks).delete();


    }

    if (count != readLocks.size()) {
      log.error("read-locks '{}' release for task '{}' failed: Could only release {} locks", lockNames, taskInfo, count);
    } else {
      log.debug("read-locks '{}' released for task '{}'", lockNames, taskInfo);
    }
    return true;
  }

  /**
   * Removes the provided write locks from the database.
   */
  private boolean releaseWriteLocks(final Collection<UUID> writeLocks, final String taskInfo, final Collection<String> lockNames) {
    checkContext();
    int count = 0;
    if (!writeLocks.isEmpty()) {

      count = DB.sqlUpdate(
          "update read_write_lock set locked = null, task_info = null, lock_time = :lockTime where id in (:ids) and locked = :handlerId")
        .setParameter("lockTime", clock.instant())
        .setParameter("ids", writeLocks)
        .setParameter("handlerId", handlerId)
        .executeNow();

    }

    if (count != writeLocks.size()) {
      log.error("write-locks '{}' release for task '{}' failed: Could only release {} locks", lockNames, taskInfo, count);
    } else {
      log.debug("write-locks '{}' released for task '{}'", lockNames, taskInfo);
    }
    return true;
  }

  private void checkContext() {
    assert Transaction.current() == null : "Transaction open";

    //Tenant tenant = Tenant.currentUnchecked();
    // assert tenant == null || tenant.equals(Tenant.ROOT_TENANT) : "Wrong tenant";
  }

  private ReadWriteLock ensureWriteLockExists(final String resourceName) {
    ReadWriteLock ret;
    try {
      ret = DB.find(ReadWriteLock.class).where().eq("name", resourceName).findOne();
      if (ret != null) {
        return ret;
      }
      ret = new ReadWriteLock()
        .setName(resourceName)
        .setLockTime(clock.instant());
      // this might fail if another Tomcat does the same thing at the same time
      DB.save(ret);
    } catch (PersistenceException e) {
      // another instance created the resource
      log.debug("censureWriteLockExists failed {}", resourceName, e);
      ret = DB.find(ReadWriteLock.class).where().eq("name", resourceName).findOne();
    }
    return ret;
  }

  /**
   * Wird alle 30 Sekunden aufgerufen und aktualisiert die zugehörigen ReadWriteLocks. Jeder 10. Aufruf (alle 5 Minuten) prüft, ob
   * veraltete ReadWriteLocks freigegeben werden können.
   */
  public void heartbeat() {
    heartbeatCount++;
    try {
      if (!locksOutMap.isEmpty() || heartbeatCount == 10) {
        if (heartbeatCount >= 0) {
          int count = DB.sqlUpdate("update read_write_lock set lock_time = :lockTime where locked = :handlerId")
            .setParameter("lockTime", clock.instant())
            .setParameter("handlerId", handlerId)
            .executeNow();
          log.debug("sent heartbeat to {} locks", count);
        }
      }
      // wir führen nur jedes 10te Mal (300 Sekunden) ein Cleanup der Locks durch.
      if (heartbeatCount == 10) {
        heartbeatCount = 0;
        // @formatter:off
        List<ReadWriteLock> staleLocks = DB.find(ReadWriteLock.class)
          .where()
          .or()
          .isNotNull("locked") // locked read or write-locks
          .isNotNull("parent") // all read-locks
          .endOr()
          // und seit 10 Minuten kein heartbeat mehr
          .le("lockTime", clock.instant().minus(10, ChronoUnit.MINUTES))
          .findList();
        // @formatter:on
        resetStaleLocks(staleLocks);
      }
    } catch (PersistenceException e) {
      heartbeatCount = -10; // 5min Pause, um Log nicht zu fluten
      log.error("could not send heartbeat", e);
    }
  }

  public static String left(@Nonnull String str, int count) {
    return count <= 0 ? "" : str.substring(0, Math.min(count, str.length()));
  }

}
