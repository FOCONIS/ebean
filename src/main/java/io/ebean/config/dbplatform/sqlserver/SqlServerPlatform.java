package io.ebean.config.dbplatform.sqlserver;

import io.ebean.BackgroundExecutor;
import io.ebean.PersistBatch;
import io.ebean.Platform;
import io.ebean.TenantContext;
import io.ebean.config.TenantDataSourceProvider;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.config.dbplatform.DbPlatformType;
import io.ebean.config.dbplatform.DbType;
import io.ebean.config.dbplatform.IdType;
import io.ebean.config.dbplatform.PlatformIdGenerator;
import io.ebean.dbmigration.ddlgeneration.platform.SqlServerDdl;

import java.sql.Types;

/**
 * Microsoft SQL Server platform.
 */
public abstract class SqlServerPlatform extends DatabasePlatform {

  public SqlServerPlatform() {
    super();
    this.platform = Platform.SQLSERVER;
    // effectively disable persistBatchOnCascade mode for SQL Server
    // due to lack of support for getGeneratedKeys in batch mode
    this.persistBatchOnCascade = PersistBatch.NONE;
    this.idInExpandedForm = true;
    this.selectCountWithAlias = true;
    this.sqlLimiter = new SqlServerSqlLimiter();
    this.basicSqlLimiter = new SqlServerBasicSqlLimiter();
    this.platformDdl = new SqlServerDdl(this);
    this.historySupport = new SqlServerHistorySupport();
    
    // Not using getGeneratedKeys as instead we will
    // batch load sequences which enables JDBC batch execution
    dbIdentity.setSupportsGetGeneratedKeys(false);
    dbIdentity.setIdType(IdType.SEQUENCE);
    dbIdentity.setSupportsSequence(true);

    this.openQuote = "[";
    this.closeQuote = "]";

    booleanDbType = Types.INTEGER;
    dbTypeMap.put(DbType.BOOLEAN, new DbPlatformType("bit default 0"));

    dbTypeMap.put(DbType.INTEGER, new DbPlatformType("integer", false));
    dbTypeMap.put(DbType.BIGINT, new DbPlatformType("numeric", 19));
    dbTypeMap.put(DbType.REAL, new DbPlatformType("float(16)"));
    dbTypeMap.put(DbType.DOUBLE, new DbPlatformType("float(32)"));
    dbTypeMap.put(DbType.TINYINT, new DbPlatformType("smallint"));
    dbTypeMap.put(DbType.DECIMAL, new DbPlatformType("numeric", 28));

    dbTypeMap.put(DbType.BLOB, new DbPlatformType("image"));
    dbTypeMap.put(DbType.CLOB, new DbPlatformType("text"));
    dbTypeMap.put(DbType.LONGVARBINARY, new DbPlatformType("image"));
    dbTypeMap.put(DbType.LONGVARCHAR, new DbPlatformType("text"));

    dbTypeMap.put(DbType.DATE, new DbPlatformType("date"));
    dbTypeMap.put(DbType.TIME, new DbPlatformType("time"));
    dbTypeMap.put(DbType.TIMESTAMP, new DbPlatformType("datetime2"));

  }

  /**
   * Returns an instance of this platform.
   */
  public static SqlServerPlatform create() {
    // FIXME: make this switchable!
    //return new SqlServer2016Platform();
    return new SqlServer2014Platform();
  }

  
  /**
   * Create a Postgres specific sequence IdGenerator.
   */
  @Override
  public PlatformIdGenerator createSequenceIdGenerator(BackgroundExecutor be,
      TenantDataSourceProvider ds, String seqName, int batchSize, boolean perTenant, TenantContext tenantContext) {

    return new SqlServerSequenceIdGenerator(be, ds, seqName, batchSize, perTenant, tenantContext);
  }
}
