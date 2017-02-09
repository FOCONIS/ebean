package io.ebeaninternal.server.core;

import io.ebean.TenantContext;
import io.ebean.bean.BeanCollection;
import io.ebean.bean.BeanCollectionLoader;
/**
 * Concrete implementation of {@link BeanCollectionLoader}.
 * @author Roland Praml, FOCONIS AG
 */
public class DefaultBeanCollectionLoader implements BeanCollectionLoader {

  private final DefaultBeanLoader beanLoader;
  private final String name;
  private final Object tenantId;
  private final TenantContext tenantContext;
  
  public DefaultBeanCollectionLoader(DefaultBeanLoader beanLoader, String name, Object tenantId, TenantContext tenantContext) {
    this.beanLoader = beanLoader;
    this.name = name;
    this.tenantId = tenantId;
    this.tenantContext = tenantContext;
  }
  
  @Override
  public void loadMany(BeanCollection<?> collection, boolean onlyIds) {
    if (tenantId == null) {
      beanLoader.loadMany(collection, onlyIds);
    } else {
      Object old = tenantContext.setTenantId(tenantId);
      try {
        beanLoader.loadMany(collection, onlyIds);
      } finally {
        tenantContext.setTenantId(old);
      }
    }
  }

  @Override
  public Object currentTenantId() {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }
}
