package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlHandler;

public class Db2DdlHandler extends BaseDdlHandler {

  public Db2DdlHandler(DatabaseConfig config, PlatformDdl platformDdl) {
    super(config, platformDdl, new Db2TableDdl(config, platformDdl));
  }
}
