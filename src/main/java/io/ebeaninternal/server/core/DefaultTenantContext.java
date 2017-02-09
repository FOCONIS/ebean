package io.ebeaninternal.server.core;

import io.ebean.TenantContext;
import io.ebean.config.CurrentTenantProvider;

public class DefaultTenantContext implements TenantContext {

  ThreadLocal<Object> holder = new ThreadLocal<>();
  
  private final CurrentTenantProvider currentTenantProvider;
  
  public DefaultTenantContext(CurrentTenantProvider currentTenantProvider) {
    this.currentTenantProvider = currentTenantProvider;
  }

  @Override
  public String translateSql(String sql) {
    return sql;
  }

  @Override
  public String translateSql(String sql, Object tenantId) {
    return sql;
  }
  
  @Override
  public Object getTenantId() {
    Object tenantId = holder.get();
    
    if (tenantId == null && currentTenantProvider != null) {
      tenantId = currentTenantProvider.currentId();
    }
    return tenantId;
  }

  @Override
  public Object setTenantId(Object tenantId) {
    Object ret = holder.get();
    holder.set(tenantId);
    return ret;
  }

  @Override
  public boolean isMultiTenant() {
    return true;
  }

}
