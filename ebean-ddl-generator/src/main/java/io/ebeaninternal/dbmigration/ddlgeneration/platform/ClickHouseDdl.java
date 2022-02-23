package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlHandler;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlOptions;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;

public class ClickHouseDdl extends PlatformDdl {

  private static final String LOG_TABLE = "ENGINE = Log()";

  public ClickHouseDdl(DatabasePlatform platform) {
    super(platform);
    this.includeStorageEngine = true;
    this.identitySuffix = "";
    this.columnNotNull = null;
  }

  @Override
  public DdlHandler createDdlHandler(DatabaseConfig config) {
    return new ClickHouseDdlHandler(config, this);
  }

  @Override
  protected String convertArrayType(String logicalArrayType) {
    return ClickHouseDbArray.logicalToNative(logicalArrayType);
  }

  /**
   * Add an table storage engine to the create table statement.
   */
  @Override
  public void tableStorageEngine(DdlBuffer apply, String storageEngine) {
    if (storageEngine == null) {
      // default to Log() table but really should all be explicit (need arguments for MergeTree etc)
      storageEngine = LOG_TABLE;
    }
    apply.append(" ").append(storageEngine);
  }

  @Override
  public void alterTableAddForeignKey(DdlBuffer buffer, DdlOptions options, WriteForeignKey request) {
  }

  @Override
  public void alterTableDropForeignKey(DdlBuffer buffer, String tableName, String fkName) {

  }

  @Override
  public void tableInlineForeignKey(DdlBuffer buffer, WriteForeignKey request) {

  }

  @Override
  public void dropIndex(DdlBuffer buffer, String indexName, String tableName, boolean concurrent) {

  }

  @Override
  public String createIndex(WriteCreateIndex create) {
    return null;
  }

  @Override
  public String createCheckConstraint(String ckName, String checkConstraint) {
    return null;
  }

  @Override
  public void addTableComment(DdlWrite writer, String tableName, String tableComment) {
    // do nothing
  }

  @Override
  public void addColumnComment(DdlWrite writer, String table, String column, String comment) {
    // do nothing
  }

  @Override
  public boolean isInlineComments() {
    return false;
  }
}
