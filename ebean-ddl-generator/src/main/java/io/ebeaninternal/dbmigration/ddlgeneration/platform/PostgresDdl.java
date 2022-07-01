package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AlterColumn;

/**
 * Postgres specific DDL.
 */
public class PostgresDdl extends PlatformDdl {

  private static final String dropIndexConcurrentlyIfExists = "drop index concurrently if exists ";

  public PostgresDdl(DatabasePlatform platform) {
    super(platform);
    this.historyDdl = new PostgresHistoryDdl();
    this.createSchemaSupport = true;
    this.dropTableCascade = " cascade";
    this.columnSetType = "type ";
    this.alterTableIfExists = "if exists ";
    this.createIndexIfNotExists = "if not exists ";
    this.columnSetNull = "drop not null";
    this.addForeignKeySkipCheck = " not valid";
    this.indexConcurrent = "concurrently ";
  }

  @Override
  public boolean addPartitionColumnToPrimaryKey() {
    return true;
  }

  @Override
  public String setLockTimeout(int lockTimeoutSeconds) {
    return "set lock_timeout = " + (lockTimeoutSeconds * 1000);
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
  public void addDefaultTablePartition(DdlBuffer apply, String tableName) {
    apply.append("create table ").append(tableName).append("_default partition of ").append(tableName).append(" default");
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
  protected void alterColumnType(DdlWrite writer, AlterColumn alter) {
    String type = convert(alter.getType());
    alterTable(writer, alter.getTableName()).append(alterColumn, alter.getColumnName())
      .append(columnSetType).append(type)
      .append(" using ").append(alter.getColumnName()).append("::").append(type);
  }
}
