package io.ebean.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Provides the Tenant Ids that are available on this server.
 */
@FunctionalInterface
public interface AvailableTenantsProvider {

  /**
   * Return the Tenant Ids that are available for this sqlConnection.
   */
  Collection<Object> getTenantIds(Connection sqlConnection) throws SQLException;
}
