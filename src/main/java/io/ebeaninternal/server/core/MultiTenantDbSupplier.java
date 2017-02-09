package io.ebeaninternal.server.core;

import io.ebean.TenantContext;
import io.ebean.config.TenantDataSourceProvider;
import io.ebeaninternal.server.transaction.DataSourceSupplier;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSource supplier based on DataSource per Tenant.
 */
class MultiTenantDbSupplier implements DataSourceSupplier {

  private final TenantContext tenantContext;

  private final TenantDataSourceProvider dataSourceProvider;

  MultiTenantDbSupplier(TenantContext tenantContext, TenantDataSourceProvider dataSourceProvider) {
    this.tenantContext = tenantContext;
    this.dataSourceProvider = dataSourceProvider;
  }

  @Override
  public DataSource getDataSource() {
    return dataSourceProvider.dataSource(tenantContext.getTenantId());
  }

  @Override
  public Connection getConnection(Object tenantId) throws SQLException {
    return dataSourceProvider.dataSource(tenantId).getConnection();
  }

  @Override
  public void shutdown(boolean deregisterDriver) {
    dataSourceProvider.shutdown(deregisterDriver);
  }
}
