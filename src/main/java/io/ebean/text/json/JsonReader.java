package io.ebean.text.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.ebean.plugin.BeanType;

/**
 * Wraps the underlying Jackson Json reader.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface JsonReader {

  /**
   * Returns the JsonVersionMigrationContext to perform json migration.
   * by {@link JsonVersionMigrationHandler}.
   */
  JsonVersionMigrationContext createContext(BeanType<?> beanType);

  /**
   * Returns the wrapped Jackson JsonParser.
   */
  JsonParser getParser();

  /**
   * Wraps a Jackson JsonParser in a new JsonReader. This may be used in migration routines.
   */
  JsonReader forJson(JsonParser moreJson, boolean resetContext);

  /**
   * Reads the next json token.
   */
  JsonToken nextToken() throws IOException;

  /**
   * Returns the wrapped object mapper.
   */
  ObjectMapper getObjectMapper();
}
