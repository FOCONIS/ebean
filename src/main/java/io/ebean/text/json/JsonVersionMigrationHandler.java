package io.ebean.text.json;


import io.ebean.plugin.BeanType;

/**
 * This handler can perform JSON migration. (Similar to migration scripts)
 *
 * @author Roland Praml, FOCONIS AG
 */
@FunctionalInterface
public interface JsonVersionMigrationHandler {

  /**
   * Creates a context to migrate a bean.
   */
  JsonVersionMigrationContext createContext(JsonReader readJson, BeanType<?> beanType);

}
