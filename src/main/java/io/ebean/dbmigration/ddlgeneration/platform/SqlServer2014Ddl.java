package io.ebean.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.DatabasePlatform;

/**
 * MS SQL Server platform specific DDL.
 */
public class SqlServer2014Ddl extends SqlServerDdl {

  public SqlServer2014Ddl(DatabasePlatform platform) {
    super(platform);
    this.historyDdl = new SqlServer2014HistoryDdl();
  }

}
