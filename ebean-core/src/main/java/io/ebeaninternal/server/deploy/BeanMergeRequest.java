package io.ebeaninternal.server.deploy;

import io.ebean.bean.EntityBean;
import io.ebean.bean.PersistenceContext;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class BeanMergeRequest {
  private static final Object DUMMY = new Object();
  private final PersistenceContext persistenceContext;
  private final Map<Object, Object> processedBeans = new IdentityHashMap<>();

  public BeanMergeRequest(PersistenceContext persistenceContext) {
    this.persistenceContext = persistenceContext;
  }

  public boolean processed(EntityBean bean) {
    return processedBeans.put(bean, DUMMY) != null;
  }

  public PersistenceContext persistenceContext() {
    return persistenceContext;
  }
}
