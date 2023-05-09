package io.ebean;

import io.ebean.bean.PersistenceContext;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class BeanMergeOptions {
  private PersistenceContext persistenceContext;

  public PersistenceContext getPersistenceContext() {
    return persistenceContext;
  }

  public void setPersistenceContext(PersistenceContext persistenceContext) {
    this.persistenceContext = persistenceContext;
  }
}
