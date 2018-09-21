package io.ebeaninternal.server.query;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ebean.annotation.Platform;

/**
 * Base class for the query plan loggers.
 *
 * set the logger 'io.ebean.QUERYPLAN' to debug or trace to log the query plan for each 'select' query.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public abstract class QueryPlanLogger {

  protected static final Logger queryplanLog = LoggerFactory.getLogger("io.ebean.QUERYPLAN");

  public abstract void logQueryPlan(Connection conn, CQueryPlan plan, CQueryPredicates predicates);

  public static final String BASE_PATH = System.getProperty("io.ebean.queryplan.dir");

  private static QueryPlanLogger explainLogger = new QueryPlanLoggerExplain();
  private static QueryPlanLogger sqlserverLogger = new QueryPlanLoggerSqlServer();
  private static QueryPlanLogger oracleLogger = new QueryPlanLoggerOracle();
  private static QueryPlanLogger nopLogger = new QueryPlanLogger() {
    @Override
    public void logQueryPlan(Connection conn, CQueryPlan plan, CQueryPredicates predicates) {
      // do nothing
    }
  };

  protected String getFilePrefix(String sql) {
    sql = sql.replaceAll("[^\\w .-]+","_");
    if (sql.length() >100) {
      sql = sql.substring(0, 100);
    }
    return BASE_PATH + "/" + sql + UUID.nameUUIDFromBytes(sql.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Returns the logger to log queries.
   */
  public static QueryPlanLogger getLogger(Platform platform) {
    if (!queryplanLog.isDebugEnabled()) {
      return nopLogger;
    }
    switch (platform) {
    case SQLSERVER:
    case SQLSERVER16:
    case SQLSERVER17:
      return sqlserverLogger;

    case ORACLE:
      return oracleLogger;

    default:
      return explainLogger;
    }
  }
}
