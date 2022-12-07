/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.model.locking;


import io.ebean.annotation.Formula;
import io.ebean.annotation.Index;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Entity to lock resources for the scheduler.
 *
 * @author Alexander Wagner, FOCONIS AG
 */
@Entity
public class ReadWriteLock {

  private static final long serialVersionUID = 1L;

  @Id
  private UUID id;

  /**
   * The Resource-Name (String).
   *
   * <pre>
   *  global
   *  tenant@1
   *  tenant@1000
   *  acl_entry@1000
   *  acl_entry@1001
   * </pre>
   */
  @Index(unique = true)
  @Size(max = 127)

  private String name;

  @Formula(select = "coalesce(${ta}.name, j${ta}.name)",
    join = "left join read_write_lock j${ta} on j${ta}.id = ${ta}.parent_id")
  private String resourceName;

  // nur bei Read-Locks
  @ManyToOne(optional = true)
  private ReadWriteLock parent;

  @OneToMany(mappedBy = "parent")
  private List<ReadWriteLock> readLocks;

  @Size(max = 255)
  private String taskInfo;

  @Index
  private UUID locked;

  @NotNull
  private Instant lockTime;

  public ReadWriteLock() {

  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getResourceName() {
    return resourceName;
  }

  public ReadWriteLock getParent() {
    return parent;
  }

  public List<ReadWriteLock> getReadLocks() {
    return readLocks;
  }

  public String getTaskInfo() {
    return taskInfo;
  }

  public UUID getLocked() {
    return locked;
  }

  public Instant getLockTime() {
    return lockTime;
  }

  public ReadWriteLock setId(UUID id) {
    this.id = id;
    return this;
  }

  public ReadWriteLock setName(String name) {
    this.name = name;
    return this;
  }

  public ReadWriteLock setResourceName(String resourceName) {
    this.resourceName = resourceName;
    return this;
  }

  public ReadWriteLock setParent(ReadWriteLock parent) {
    this.parent = parent;
    return this;
  }

  public ReadWriteLock setReadLocks(List<ReadWriteLock> readLocks) {
    this.readLocks = readLocks;
    return this;
  }

  public ReadWriteLock setTaskInfo(String taskInfo) {
    this.taskInfo = taskInfo;
    return this;
  }

  public ReadWriteLock setLocked(UUID locked) {
    this.locked = locked;
    return this;
  }

  public ReadWriteLock setLockTime(Instant lockTime) {
    this.lockTime = lockTime;
    return this;
  }
}
