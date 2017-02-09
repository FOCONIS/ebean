package org.tests.basic;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.avaje.datasource.DataSourceConfig;
import org.avaje.datasource.DataSourcePool;
import org.avaje.datasource.pool.ConnectionPool;

import io.ebean.Ebean;
import io.ebean.config.PropertyMap;
import io.ebean.config.TenantDataSourceProvider;
import io.ebean.dbmigration.DdlGenerator;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.server.transaction.JdbcTransaction;

public class MyTenantDataSourceProvider implements TenantDataSourceProvider {

  ConcurrentMap<Object, DataSourcePool> datasources = new ConcurrentHashMap<>();
  
  @Override
  public DataSource dataSource(Object tenantId) {
    return datasources.computeIfAbsent(tenantId, this::createPool);
  }

  protected DataSourcePool createPool(Object tenantId) {
    SpiEbeanServer server = (SpiEbeanServer) Ebean.getDefaultServer();
    String name = server.getName();
    name = name + "." + tenantId;
    DataSourceConfig config = new DataSourceConfig();
    Properties prop = PropertyMap.defaultProperties();
    config.loadSettings(prop, name);
        
    final ConnectionPool pool = new ConnectionPool(name, config);
    AtomicInteger counter = new AtomicInteger();
    
    
    DdlGenerator ddlGenerator = new DdlGenerator(server, server.getServerConfig()) {
      @Override
      protected io.ebean.Transaction createTransaction() {
        try {
          return new JdbcTransaction("mig-" + counter.getAndIncrement(), false, pool.getConnection(), null);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      };
    };
    
    ddlGenerator.runDdl();
    
    return pool;
    
  }
  @Override
  public void shutdown(boolean deregisterDriver) {
    datasources.values().forEach(dataSource -> dataSource.shutdown(deregisterDriver));
  }

}
