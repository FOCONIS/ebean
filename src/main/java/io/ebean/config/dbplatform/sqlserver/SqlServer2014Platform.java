package io.ebean.config.dbplatform.sqlserver;

/**
 * SQL Server platform 2014
 */
public class SqlServer2014Platform extends SqlServerPlatform {

  public SqlServer2014Platform() {
    this.historySupport = new SqlServer2014HistorySupport();
  }
}
