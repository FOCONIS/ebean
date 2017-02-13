package io.ebean.config.dbplatform.sqlserver;

import io.ebean.dbmigration.ddlgeneration.platform.SqlServer2014Ddl;

/**
 * SQL Server platform 2014
 */
public class SqlServer2014Platform extends SqlServerPlatform {

  public SqlServer2014Platform() {
    this.historySupport = new SqlServer2014HistorySupport();
    this.platformDdl = new SqlServer2014Ddl(this);
  }
}
