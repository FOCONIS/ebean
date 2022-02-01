package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import java.io.IOException;

import io.ebean.config.DatabaseConfig;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;

public class Db2TableDdl extends BaseTableDdl {

  private static final String MOVE_TABLE = "CALL SYSPROC.ADMIN_MOVE_TABLE(CURRENT_SCHEMA,'%s','%s','%s','%s','','','','','','MOVE')";

  public Db2TableDdl(DatabaseConfig config, PlatformDdl platformDdl) {
    super(config, platformDdl);
  }

  @Override
  protected void writeTablespaceChange(DdlBuffer buffer, String tablename, String tableSpace, String indexSpace,
      String lobSpace) throws IOException {
    private static final String MOVE_TABLE = "CALL SYSPROC.ADMIN_MOVE_TABLE(CURRENT_SCHEMA,'%s','%s','%s','%s','','','','','','MOVE')";
    buffer.appendStatement(String.format(MOVE_TABLE, tablename, tableSpace, indexSpace, lobSpace));
  }

}
