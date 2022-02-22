package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AddHistoryTable;
import io.ebeaninternal.dbmigration.migration.DropHistoryTable;
import io.ebeaninternal.dbmigration.model.MTable;

/**
 * History DDL for MariaDB.
 */
public class MariaDbHistoryDdl implements PlatformHistoryDdl {

  private PlatformDdl platformDdl;

  @Override
  public void configure(DatabaseConfig config, PlatformDdl platformDdl) {
    this.platformDdl = platformDdl;
  }


  @Override
  public void createWithHistory(DdlWrite writer, MTable table) {
    String baseTable = table.getName();
    enableSystemVersioning(writer, baseTable);
  }

  private void enableSystemVersioning(DdlWrite writer, String baseTable) {
    platformDdl.alterTable(writer, baseTable).add("add system versioning");
    writer.dropAll().append("alter table ").append(baseTable).append(" drop system versioning").endOfStatement();
  }

  @Override
  public void dropHistoryTable(DdlWrite writer, DropHistoryTable dropHistoryTable) {
    String baseTable = dropHistoryTable.getBaseTable();
    platformDdl.alterTable(writer, baseTable).add("drop system versioning");
  }

  @Override
  public void addHistoryTable(DdlWrite writer, AddHistoryTable addHistoryTable) {
    String baseTable = addHistoryTable.getBaseTable();
    enableSystemVersioning(writer, baseTable);
  }

  @Override
  public boolean alterHistoryTables() {
    return false;
  }
}
