package io.ebean.platform.sqlserver;

import io.ebean.annotation.Platform;
import io.ebean.config.dbplatform.DbPlatformType;
import io.ebean.config.dbplatform.DbType;
import io.ebean.config.dbplatform.IdType;

/**
 * Microsoft SQL Server platform that has non-UTF8 types (char, varchar, text) and default to Identity rather than Sequence.
 */
public class SqlServer16Platform extends SqlServerBasePlatform {

  public SqlServer16Platform() {
    super();
    this.platform = Platform.SQLSERVER16;
    // default to use Identity rather than sequences
    this.dbIdentity.setIdType(IdType.IDENTITY);

    // non-utf8 column types
    dbTypeMap.put(DbType.CHAR, new DbPlatformType("char", 1));
    dbTypeMap.put(DbType.VARCHAR, new DbPlatformType("varchar", 255));
    dbTypeMap.put(DbType.LONGVARCHAR, new DbPlatformType("text", false));
    dbTypeMap.put(DbType.CLOB, new DbPlatformType("text", false));
    dbTypeMap.put(DbType.JSON, new DbPlatformType("text", false));
    dbTypeMap.put(DbType.JSONB, new DbPlatformType("text", false));
    dbTypeMap.put(DbType.BLOB, new DbPlatformType("image", false));
    dbTypeMap.put(DbType.LONGVARBINARY, new DbPlatformType("image", false));
  }

}
