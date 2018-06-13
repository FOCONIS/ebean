package io.ebean.text.json;

import java.io.IOException;

import io.ebean.plugin.BeanType;

/**
 * This handler can perform JSON migration. (Similar to migration scripts)
 *
 * @author Roland Praml, FOCONIS AG
 */

public interface JsonVersionMigrationContext {

  /**
   * Migrates a concrete bean of <code>beanType</code>.
   */
  void migrate(BeanType<?> beanType) throws IOException;

  /**
   * Migrates a root bean of an inheritance tree. <code>beanType</code> is the root of the inheritance tree.
   */

  void migrateRoot() throws IOException;

  /**
   * Reads the version from the json stream for this bean type.
   */
  void parseVersion() throws IOException;

  /**
   * Returns the jsonReader that should be used to read the rest of the bean.
   */
  JsonReader getJsonReader() throws IOException;
}
