package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlOptions;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;

/**
 * DB2 platform specific DDL.
 */
public class SQLiteDdl extends PlatformDdl {

  public SQLiteDdl(DatabasePlatform platform) {
    super(platform);
    this.identitySuffix = "";
    this.inlineForeignKeys = true;
  }

  @Override
  public void addTableComment(DdlWrite writer, String tableName, String tableComment) {
    // not supported
  }

  @Override
  public void addColumnComment(DdlWrite writer, String table, String column, String comment) {
    // not supported
  }

  @Override
  public void alterTableAddForeignKey(DdlBuffer buffer, DdlOptions options, WriteForeignKey request) {
    // not supported
  }
}
