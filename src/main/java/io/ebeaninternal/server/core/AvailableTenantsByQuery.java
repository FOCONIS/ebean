package io.ebeaninternal.server.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ebean.config.AvailableTenantsProvider;
import io.ebean.config.dbplatform.h2.H2HistoryTrigger;

/**
 * A tenantProvider that executes the SQL query and returns a list of available tenants.
 * @author Roland Praml, FOCONIS AG
 *
 */
public class AvailableTenantsByQuery implements AvailableTenantsProvider {

  private static final Logger logger = LoggerFactory.getLogger(H2HistoryTrigger.class);

  private final String query;
  Set<Object> tenants = new HashSet<>();
  public AvailableTenantsByQuery(String query) {
    super();
    this.query = query;
  }


  @Override
  public Collection<Object> getTenantIds(Connection sqlConnection) throws SQLException {
    Statement stmt = sqlConnection.createStatement();
    ResultSet result = stmt.executeQuery(query);
    while (result.next()) {
      String tenantId = result.getString(1);
      logger.debug("Creating tenant instance {}", tenantId);
      tenants.add(tenantId);
    }
    return tenants;
  }

}
