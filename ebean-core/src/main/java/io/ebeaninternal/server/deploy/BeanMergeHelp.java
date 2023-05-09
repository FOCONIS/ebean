package io.ebeaninternal.server.deploy;

import io.ebean.BeanMergeOptions;
import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.bean.PersistenceContext;
import io.ebeaninternal.server.json.PathStack;
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
  private final PathStack pathStack;

  private final boolean mergeId;

  private final boolean mergeVersion;

  private final boolean clearCollections;

  private final boolean addExistingToPersistenceContext;


  private final BeanMergeOptions.MergeCheck mergeCheck;


  public BeanMergeHelp(EntityBean rootBean, BeanMergeOptions options) {
    this.persistenceContext = extractPersistenceContext(rootBean, options);
    if (options == null) {
      this.mergeCheck = null;
      this.pathStack = null;
      this.mergeId = true;
      this.mergeVersion = false;
      this.clearCollections = true;
      this.addExistingToPersistenceContext = true;
    } else {
      this.mergeCheck = options.getMergeCheck();
      this.pathStack = mergeCheck == null ? null : new PathStack();
      this.mergeId = options.isMergeId();
      this.mergeVersion = options.isMergeVersion();
      this.clearCollections = options.isClearCollections();
      this.addExistingToPersistenceContext = true;
    }
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

  public boolean addExistingToPersistenceContext() {
    return addExistingToPersistenceContext;
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


  boolean checkMerge(BeanProperty property, EntityBean bean, EntityBean existing) {
    if (!bean._ebean_getIntercept().isLoadedProperty(property.propertyIndex())) {
      return false;
    }
    if (property.isId() && !mergeId) {
      return false;
    }
    if (property.isVersion() && !mergeVersion) {
      return false;
    }
    return mergeCheck == null || mergeCheck.mergeBeans(bean, existing, property, pathStack.peekWithNull());

  }

  public void pushPath(String name) {
    if (pathStack != null) {
      pathStack.pushPathKey(name);
    }
  }

  public void popPath() {
    if (pathStack != null) {
      pathStack.pop();
    }
  }

  public boolean clearCollections() {
    return clearCollections;
  }

  public void mergeBeans(BeanDescriptor<?> desc, EntityBean bean, EntityBean existing) {
    if (processed(bean)) {
      return;
    }
    if (addExistingToPersistenceContext()) {
      contextPutIfAbsent(desc, existing);
    }

    EntityBeanIntercept fromEbi = bean._ebean_getIntercept();

    for (BeanProperty prop : desc.propertiesAll()) {
      if (checkMerge(prop, bean, existing)) {
        prop.merge(bean, existing, this);
      }
    }
  }
}
