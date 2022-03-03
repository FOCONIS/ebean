package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
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
  private boolean alterHistorySet = false;

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

    DdlBuffer drop = writer.dropAll();
    drop.append("alter table ").append(baseTable).append(" drop system versioning").endOfStatement();
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
  public void regenerateHistory(DdlWrite writer, String tableName) {
    MTable table = writer.getTable(tableName);
    if (table != null && table.isWithHistory() && !alterHistorySet) {
      writer.apply().appendStatement("SET @@system_versioning_alter_history = 1");
      alterHistorySet = true;
    }
  }

  @Override
  public boolean alterHistoryTables() {
    return false;
  }
}
