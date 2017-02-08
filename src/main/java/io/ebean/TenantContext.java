package io.ebean;

public interface TenantContext {
  
  String translateSql(String sql);
  
  String translateSql(String sql, Object tenantId);

  Object getTenantId();
  
  void push(Object tenantId);
  
  Object pop();

}
