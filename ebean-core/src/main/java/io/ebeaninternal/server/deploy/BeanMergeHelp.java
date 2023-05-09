package io.ebeaninternal.server.deploy;

import io.ebean.BeanMergeOptions;
import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.bean.PersistenceContext;
import io.ebeaninternal.server.transaction.DefaultPersistenceContext;

import java.util.IdentityHashMap;
import java.util.Map;

import static io.ebeaninternal.server.persist.DmlUtil.isNullOrZero;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class BeanMergeHelp {
  private static final Object DUMMY = new Object();
  private final PersistenceContext persistenceContext;
  private final Map<Object, Object> processedBeans = new IdentityHashMap<>();
  private final BeanMergeOptions options;

  public BeanMergeHelp(EntityBean rootBean, BeanMergeOptions options) {
    this.persistenceContext = extractPersistenceContext(rootBean, options);
    this.options = options;
  }

  private PersistenceContext extractPersistenceContext(EntityBean rootBean, BeanMergeOptions options) {
    PersistenceContext pc = options == null ? null : options.getPersistenceContext();
    if (pc == null) {
      pc = rootBean._ebean_getIntercept().persistenceContext();
    }
    if (pc == null) {
      pc = new DefaultPersistenceContext();
    }
    return pc;
  }


  public boolean processed(EntityBean bean) {
    return processedBeans.put(bean, DUMMY) != null;
  }

  public PersistenceContext persistenceContext() {
    return persistenceContext;
  }

  public boolean addExisting() {
    return true;
  }

  public EntityBean contextPutIfAbsent(BeanDescriptor<?> desc, EntityBean bean) {
    if (bean == null) {
      return null;
    }
    Object id = desc.id(bean);
    if (!isNullOrZero(id)) {
      return desc.contextPutIfAbsent(persistenceContext, id, bean);
    }
    return null;
  }

  public void pushBeans(EntityBean bean, EntityBean existing) {
  }

  public void popBeans() {
  }

  public boolean merge(BeanProperty property) {
    return !property.isVersion();
  }

  public void pushPath(String name) {
  }

  public void popPath() {
  }

  public boolean clearCollection() {
    return true;
  }

   public void mergeBeans(BeanDescriptor<?> desc, EntityBean bean, EntityBean existing) {
    if (processed(bean)) {
      return;
    }
    if (addExisting()) {
      contextPutIfAbsent(desc, existing);
    }

    EntityBeanIntercept fromEbi = bean._ebean_getIntercept();

    for (BeanProperty prop : desc.propertiesAll()) {
      if (!prop.isVersion() && fromEbi.isLoadedProperty(prop.propertyIndex())) {
        prop.merge(bean, existing, this);
      }
    }

    popBeans();
  }
}
