package io.ebeaninternal.server.core;

import java.util.ArrayDeque;
import java.util.Deque;

import io.ebean.TenantContext;
import io.ebean.config.CurrentTenantProvider;

public class DefaultTenantContext implements TenantContext {

  ThreadLocal<Deque<Object>> tenantStack = new ThreadLocal<Deque<Object>>() {
    @Override
    protected Deque<Object> initialValue() {
      return new ArrayDeque<>();
    };
  };
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
    Deque<Object> stack = tenantStack.get();
    if (stack.isEmpty()) {
      if (currentTenantProvider == null) {
        return null;
      } else {
        return currentTenantProvider.currentId();
      }
    }
    return stack.peek();
  }

  @Override
  public void push(Object tenantId) {
    tenantStack.get().push(tenantId);
  }

  @Override
  public Object pop() {
    return tenantStack.get().pop();
  }

}
