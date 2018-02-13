package io.ebean.config.dbplatform.sqlserver;

import io.ebean.config.dbplatform.SqlLimitRequest;
import io.ebean.config.dbplatform.SqlLimitResponse;
import io.ebean.config.dbplatform.SqlLimiter;

/**
 * Use ANSI offset rows syntax or top n - SQL Server 2012 onwards.
 */
public class SqlServerSqlLimiter implements SqlLimiter {

  public SqlServerSqlLimiter() {
  }

  @Override
  public SqlLimitResponse limit(SqlLimitRequest request) {

    String dbSql = request.getDbSql();
    StringBuilder sb = new StringBuilder(150 + dbSql.length());

    int firstRow = request.getFirstRow();
    int maxRows = request.getMaxRows();

    if (firstRow < 1) {
      // just use top n
      sb.append("select ");
      if (request.isDistinct()) {
        sb.append("distinct ");
      }
      sb.append("top ").append(maxRows).append(" ");
      sb.append(dbSql);
    } else {

      sb.append("select ");
      if (request.isDistinct()) {
        sb.append("distinct ");
      }

      sb.append(dbSql);
      if (firstRow > 0) {
        sb.append(" ").append("offset");
        sb.append(" ").append(firstRow).append(" rows");
      }
      if (maxRows > 0) {
        sb.append(" fetch next ").append(maxRows).append(" rows only");
      }
    }
    String sql = request.getDbPlatform().completeSql(sb.toString(), request.getOrmQuery());
    return new SqlLimitResponse(sql, false);
  }

}
