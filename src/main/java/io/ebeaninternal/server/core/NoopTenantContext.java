package io.ebeaninternal.server.core;

import io.ebean.TenantContext;
/**
 * A No-Op Tenant context, if tenant support is not enabled.
 * 
 * @author Roland Praml, FOCONIS AG
 */
public class NoopTenantContext implements TenantContext {

  @Override
  public String translateSql(String sql) {
    return sql;
  }

  @Override
  public Object getTenantId() {
    return null;
  }

  @Override
  public void push(Object tenantId) {
    if (tenantId != null) {
      throw new IllegalArgumentException("Tenant support is not enabled");
    }
  }

  @Override
  public Object pop() {
    return null;
  }

}
