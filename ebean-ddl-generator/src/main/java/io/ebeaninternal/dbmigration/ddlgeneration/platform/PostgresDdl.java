package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;

/**
 * Postgres specific DDL.
 */
public class PostgresDdl extends PlatformDdl {

  private static final String dropIndexConcurrentlyIfExists = "drop index concurrently if exists ";

  public PostgresDdl(DatabasePlatform platform) {
    super(platform);
    this.historyDdl = new PostgresHistoryDdl();
    this.dropTableCascade = " cascade";
    this.columnSetType = "type ";
    this.alterTableIfExists = "if exists ";
    this.createIndexIfNotExists = "if not exists ";
    this.columnSetNull = "drop not null";
    this.addForeignKeySkipCheck = " not valid";
    this.indexConcurrent = "concurrently ";
  }

  @Override
  public String setLockTimeout(int lockTimeoutSeconds) {
    return "set lock_timeout = " + (lockTimeoutSeconds * 1000);
  }

  @Override
  public boolean suppressPrimaryKeyOnPartition() {
    return true;
  }

  @Override
  protected String convertArrayType(String logicalArrayType) {
    return NativeDbArray.logicalToNative(logicalArrayType);
  }

  @Override
  public void addTablePartition(DdlBuffer apply, String partitionMode, String partitionColumn) {
    apply.append(" partition by range (").append(partitionColumn).append(")");
  }

  @Override
  public String dropIndex(String indexName, String tableName, boolean concurrent) {
    return (concurrent ? dropIndexConcurrentlyIfExists : dropIndexIfExists) + maxConstraintName(indexName);
  }

  /**
   * Modify and return the column definition for autoincrement or identity definition.
   */
  @Override
  public String asIdentityColumn(String columnDefn, DdlIdentity identity) {
    return asIdentityStandardOptions(columnDefn, identity);
  }

  @Override
  public void alterColumnType(DdlWrite write, String tableName, String columnName, String type) {
    write.alterTable(tableName, alterColumn).append(" ").append(columnName).append(" ")
        .append(columnSetType).append(convert(type)).append(alterColumnSuffix)
        .append(" using ").append(columnName).append("::").append(convert(type));
  }
}
