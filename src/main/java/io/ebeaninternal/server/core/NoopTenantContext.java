package io.ebeaninternal.server.core;

import io.ebean.TenantContext;
import io.ebean.util.TenantUtil;
/**
 * A No-Op Tenant context, if tenant support is not enabled.
 * 
 * @author Roland Praml, FOCONIS AG
 */
public class NoopTenantContext implements TenantContext {

  @Override
  public String translateSql(String sql) {
    return TenantUtil.applySchemas(sql, null, null);
  }

  @Override
  public String translateSql(String sql, Object tenantId) {
    return TenantUtil.applySchemas(sql, null, null);
  }
  
  @Override
  public Object getTenantId() {
    return null;
  }

  @Override
  public Object setTenantId(Object tenantId) {
    return null;
  }

  @Override
  public boolean isMultiTenant() {
    return false;
  }

}
