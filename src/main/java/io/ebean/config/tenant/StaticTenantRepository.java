package io.ebean.config.tenant;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.ebean.config.PropertiesWrapper;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.util.StringHelper;

/**
 * A static tenant repository that is initialized by a given list of ids;
 * @author Roland Praml, FOCONIS AG
 *
 */
public class StaticTenantRepository implements TenantRepository {

  private List<TenantEntity> tenants;
  
  @Override
  public void configure(DatabasePlatform platform, PropertiesWrapper prop) {
    String ids = prop.get("ids");
    tenants = Collections.unmodifiableList(
        Arrays.stream(StringHelper.delimitedToArray(ids, ",", false))
        .map(TenantEntity::new)
        .collect(Collectors.toList())
        );

  }
  
  @Override
  public void init(Connection sqlConnection) throws SQLException {
   
  }

  @Override
  public List<TenantEntity> getTenants(Connection sqlConnection) throws SQLException {
    return tenants;
  }

  @Override
  public boolean addTenant(Connection sqlConnection, TenantEntity tenantId) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeTenant(Connection sqlConnection, Object tenantId) throws SQLException {
    throw new UnsupportedOperationException();
  }

}
