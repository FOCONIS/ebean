package io.ebean.config.tenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ebean.config.PropertiesWrapper;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.dbmigration.ddl.DdlRunner;
import io.ebean.util.StringHelper;

/**
 * A TenantRepository that executes the SQL query and returns a list of available tenants.
 * @author Roland Praml, FOCONIS AG
 *
 */
public class QueryTenantRepository implements TenantRepository {

  private static final Logger logger = LoggerFactory.getLogger(QueryTenantRepository.class);

  protected String init;
  
  protected String query;

  protected String create;

  protected String delete;
  
  
  @Override
  public void configure(DatabasePlatform platform, PropertiesWrapper prop) {
    init = prop.get("init");
    query = prop.get("query");
    create = prop.get("create");
    delete = prop.get("delete");
  }
  
  @Override
  public void init(Connection sqlConnection) throws SQLException {

    logger.info("QueryTenantRepository: init: {}, query: {}, create: {}, delete  {}", init, query, create, delete);
    if (StringHelper.isNull(init)) {
      return;
    }
    // expect errors, noramlly we would execute a simple create table statement
    DdlRunner runner = new DdlRunner(true, getClass().getSimpleName()+".init");
    runner.runAll(init, sqlConnection);
  }
  



  @Override
  public List<TenantEntity> getTenants(Connection sqlConnection) throws SQLException {
    List<TenantEntity> ret = new ArrayList<>();
    
    PreparedStatement stmt = sqlConnection.prepareStatement(query);
    try {
      ResultSet result = stmt.executeQuery();
      while (result.next()) {
        ret.add(map(result));
      }
      logger.debug("Read tenants from database {}", ret);
      return ret;
      
    } finally {
      stmt.close();
    }
  }
  
  /**
   * Maps the resultSet to a {@link TenantEntity}.
   */
  protected TenantEntity map(ResultSet result) throws SQLException {
    return new TenantEntity(result.getObject(1));
  }

  /**
   * Maps the {@link TenantEntity} to a resultSet
   */
  protected void map(TenantEntity tenant, PreparedStatement stmt) throws SQLException {
    stmt.setObject(1, tenant.getId());
  }


  @Override
  public boolean addTenant(Connection sqlConnection, TenantEntity tenant) throws SQLException {
    if (StringHelper.isNull(create)) {
      throw new UnsupportedOperationException();
    }
    PreparedStatement stmt = sqlConnection.prepareStatement(query);
    try {
      map(tenant, stmt);
      return stmt.executeUpdate() == 1;
    } finally {
      stmt.close();
    }
  }


  @Override
  public boolean removeTenant(Connection sqlConnection, Object tenantId) throws SQLException {
    if (StringHelper.isNull(delete)) {
      throw new UnsupportedOperationException();
    }
    PreparedStatement stmt = sqlConnection.prepareStatement(delete);
    try {
      stmt.setObject(1, tenantId);
      return stmt.executeUpdate() == 1;
    } finally {
      stmt.close();
    }
  }

}
