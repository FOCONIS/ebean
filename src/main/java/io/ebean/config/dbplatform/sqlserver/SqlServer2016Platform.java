package io.ebean.config.dbplatform.sqlserver;

import io.ebean.dbmigration.ddlgeneration.platform.SqlServer2016Ddl;

/**
 * SQL Server platform that uses SQL-HISTORY features
 */
public class SqlServer2016Platform extends SqlServer2014Platform {

  public SqlServer2016Platform() {
    this.platformDdl = new SqlServer2016Ddl(this);
  }
}
