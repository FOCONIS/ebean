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
   * Reads the version from the json stream for this bean type. This method is called first.
   */
  void parseVersion() throws IOException;

  /**
   * Migrates a root bean of an inheritance tree. <code>beanType</code> is the root of the inheritance tree.
   * This method is called after parseVersion if inheritInfo is present.
   */

  void migrateRoot() throws IOException;

  /**
   * Migrates a concrete bean of <code>beanType</code>.
   */
  void migrate(BeanType<?> beanType) throws IOException;

  /**
   * Returns the jsonReader that should be used to read the rest of the bean.
   */
  JsonReader getJsonReader() throws IOException;
}
