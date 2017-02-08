package io.ebean;

public interface TenantContext {
  
  String translateSql(String sql);
  
  Object getTenantId();
  
  void push(Object tenantId);
  
  Object pop();

}
