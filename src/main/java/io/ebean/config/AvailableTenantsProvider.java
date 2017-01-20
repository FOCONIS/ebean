package io.ebean.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides the Tenant Id for the current request based on the current user.
 */
@FunctionalInterface
public interface AvailableTenantsProvider {

  /**
   * Return the Tenant Ids that are available for this sqlConnection.
   */
  List<Object> getTenantIds(Connection sqlConnection) throws SQLException;
}
