package io.ebean.text.json;


import java.io.IOException;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.ebean.plugin.BeanType;

/**
 * This handler can perform JSON migration. (Similar to migration scripts)
 *
 * @author Roland Praml, FOCONIS AG
 */
public interface JsonVersionMigrationHandler {

  /**
   * Migrates a root bean of an inheritance tree. <code>beanType</code> is the root of the inheritance tree.
   * This method is only for beans, that have an inheritance.
   */
  @Nonnull
  ObjectNode migrateRoot(@Nonnull ObjectNode node, @Nonnull ObjectMapper mapper, @Nonnull BeanType<?> rootBeanType) throws IOException;

  /**
   * Migrates a concrete bean of <code>beanType</code>.
   */
  @Nonnull
  ObjectNode migrate(@Nonnull ObjectNode node, @Nonnull ObjectMapper mapper, @Nonnull BeanType<?> beanType) throws IOException;

}
