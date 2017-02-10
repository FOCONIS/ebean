package io.ebean.config.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.ebean.config.PropertiesWrapper;
import io.ebean.config.dbplatform.DatabasePlatform;

/**
 * Provides a tenant repository which tenants are available on this server.
 */
public interface TenantRepository {

  /**
   * Configures the tenant repository based on platform and properties.
   */
  void configure(DatabasePlatform platform, PropertiesWrapper prop);
  
  /**
   * Initializes the tenants (e.g. create table if not exists).
   * @throws SQLException 
   */
  void init(Connection sqlConnection) throws SQLException;
  
  /**
   * Return the Tenant Ids that are available for this sqlConnection.
   * 
   */
  List<TenantEntity> getTenants(Connection sqlConnection) throws SQLException;

  /**
   * stores a tenant.
   */
  boolean addTenant(Connection sqlConnection, TenantEntity tenant) throws SQLException;
  
  /**
   * removes a tenant by its id.
   */
  boolean removeTenant(Connection sqlConnection, Object tenantId) throws SQLException;
}
