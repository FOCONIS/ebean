package io.ebean.dbmigration;

import java.util.function.Predicate;

import io.ebean.config.TenantMode;
import io.ebean.plugin.BeanType;

/**
 * Enum to classify the DB schema in {@link TenantMode#SCHEMA}
 * @author Roland Praml, FOCONIS AG
 */
public enum DbSchemaType {
  ALL(bean->true),
  SHARED(bean->bean.isSharedEntity()),
  TENANT(bean->!bean.isSharedEntity())
  ;
  
  private final Predicate<BeanType<?>> filter;
  
  private DbSchemaType(Predicate<BeanType<?>> filter) {
    this.filter = filter;
  }

  public Predicate<BeanType<?>> getFilter() {
    return filter;
  }
}
