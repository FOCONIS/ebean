package io.ebean.dbmigration;

import java.util.function.Predicate;

import io.ebean.plugin.BeanType;

public enum TenantBeanType {
  ALL(bean->true),
  SHARED(bean->bean.isSharedEntity()),
  TENANT(bean->!bean.isSharedEntity())
  ;
  
  private final Predicate<BeanType<?>> filter;
  
  private TenantBeanType(Predicate<BeanType<?>> filter) {
    this.filter = filter;
  }

  public Predicate<BeanType<?>> getFilter() {
    return filter;
  }
}
