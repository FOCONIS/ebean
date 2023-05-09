package io.ebean;

import io.ebean.bean.PersistenceContext;
import io.ebean.plugin.Property;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class BeanMergeOptions {

  @FunctionalInterface
  public interface MergeCheck<T> {
    boolean mergeBeans(T bean, T existing, Property property, String path);

  }

  private PersistenceContext persistenceContext;

  private MergeCheck<?> mergeCheck;

  private boolean mergeId = true;

  private boolean mergeVersion = false;

  private boolean clearCollections = true;

  private boolean addExistingToPersistenceContext = true;

  public PersistenceContext getPersistenceContext() {
    return persistenceContext;
  }

  public void setPersistenceContext(PersistenceContext persistenceContext) {
    this.persistenceContext = persistenceContext;
  }

  public MergeCheck<?> getMergeCheck() {
    return mergeCheck;
  }

  public <T> void setMergeCheck(MergeCheck<T> mergeCheck) {
    this.mergeCheck = mergeCheck;
  }

  public boolean isMergeId() {
    return mergeId;
  }

  public void setMergeId(boolean mergeId) {
    this.mergeId = mergeId;
  }

  public boolean isMergeVersion() {
    return mergeVersion;
  }

  public void setMergeVersion(boolean mergeVersion) {
    this.mergeVersion = mergeVersion;
  }

  public boolean isClearCollections() {
    return clearCollections;
  }

  public void setClearCollections(boolean clearCollections) {
    this.clearCollections = clearCollections;
  }

  public boolean isAddExistingToPersistenceContext() {
    return addExistingToPersistenceContext;
  }

  public void setAddExistingToPersistenceContext(boolean addExistingToPersistenceContext) {
    this.addExistingToPersistenceContext = addExistingToPersistenceContext;
  }
}
