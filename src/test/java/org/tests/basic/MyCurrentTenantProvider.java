package org.tests.basic;

import io.ebean.config.CurrentTenantProvider;

public class MyCurrentTenantProvider implements CurrentTenantProvider {

  private static ThreadLocal<String> tenant = new ThreadLocal<>();
  
  @Override
  public Object currentId() {
    return tenant.get();
  }
  
  public static void setTenantId(String tenantId) {
    tenant.set(tenantId);
  }
 
}

