package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AddHistoryTable;
import io.ebeaninternal.dbmigration.migration.DropHistoryTable;
import io.ebeaninternal.dbmigration.model.MColumn;
import io.ebeaninternal.dbmigration.model.MTable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HanaHistoryDdl implements PlatformHistoryDdl {

  private String systemPeriodStart;
  private String systemPeriodEnd;
  private PlatformDdl platformDdl;
  private String historySuffix;
  private final AtomicInteger counter = new AtomicInteger(0);
  private Map<String, String> createdHistoryTables = new ConcurrentHashMap<>();

  @Override
  public void configure(DatabaseConfig config, PlatformDdl platformDdl) {
    this.systemPeriodStart = config.getAsOfSysPeriod() + "_start";
    this.systemPeriodEnd = config.getAsOfSysPeriod() + "_end";
    this.platformDdl = platformDdl;
    this.historySuffix = config.getHistoryTableSuffix();
  }

  @Override
  public void createWithHistory(DdlWrite writer, MTable table) {
    String tableName = table.getName();
    String historyTableName = tableName + historySuffix;
    if (writer.applyHistoryView().isEmpty()) {
      createdHistoryTables.clear();
    }

    createHistoryTable(writer.apply(), table);

    // enable system versioning
    writer.alterTable(tableName, "add (\n")
      .append("    ").append(systemPeriodStart).append(" TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START, \n")
      .append("    ").append(systemPeriodEnd).append(" TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END\n")
      .append(")");

    writer.alterTable(tableName, "add period for system_time(").append(systemPeriodStart).append(",").append(systemPeriodEnd).append(")");

    enableSystemVersioning(writer, tableName, historyTableName, true, false);

    createdHistoryTables.put(tableName, historyTableName);
    
    // Workaround for drop all script
    BaseDdlWrite tmpWrite = new BaseDdlWrite();
    dropHistoryTable(tmpWrite, tableName, historyTableName);
    writer.dropAll().append(tmpWrite.toString());
    
  }

  private void createHistoryTable(DdlBuffer apply, MTable table) {
    apply.append(platformDdl.getCreateTableCommandPrefix()).append(" ").append(table.getName()).append(historySuffix).append(" (").newLine();

    // create history table
    Collection<MColumn> cols = table.allColumns();
    for (MColumn column : cols) {
      if (!column.isDraftOnly()) {
        writeColumnDefinition(apply, column.getName(), column.getType(), column.getDefaultValue(), column.isNotnull(),
          column.isIdentity() ? platformDdl.identitySuffix : null);
        apply.append(",").newLine();
      }
    }
    writeColumnDefinition(apply, systemPeriodStart, "TIMESTAMP", null, false, null);
    apply.append(",").newLine();
    writeColumnDefinition(apply, systemPeriodEnd, "TIMESTAMP", null, false, null);
    apply.newLine().append(")").endOfStatement();
  }

  @Override
  public void dropHistoryTable(DdlWrite writer, DropHistoryTable dropHistoryTable) {
    dropHistoryTable(writer, dropHistoryTable.getBaseTable(),
      dropHistoryTable.getBaseTable() + historySuffix);
  }

  protected void dropHistoryTable(DdlWrite writer, String baseTable, String historyTable) {
    // disable system versioning
    disableSystemVersioning(writer, baseTable);
    writer.alterTable(baseTable, "drop period for system_time");

    // drop the period columns
    writer.alterTable(baseTable, "drop (").append(systemPeriodStart).append(",").append(systemPeriodEnd).append(")");

    // drop the history table
    writer.postAlter().append("drop table ").append(historyTable).append(" cascade").endOfStatement();
  }

  @Override
  public void addHistoryTable(DdlWrite writer, AddHistoryTable addHistoryTable) {
    MTable table = writer.getTable(addHistoryTable.getBaseTable());
    if (table == null) {
      throw new IllegalStateException("MTable " + addHistoryTable.getBaseTable() + " not found in writer? (required for history DDL)");
    }
    createWithHistory(writer, table);
  }

  @Override
  public void updateTriggers(DdlWrite writer, HistoryTableUpdate baseTable) {
    // nothing to do
  }

  protected void writeColumnDefinition(DdlBuffer buffer, String columnName, String type, String defaultValue,
                                       boolean isNotNull, String generated) {

    String platformType = platformDdl.convert(type);
    buffer.append(" ").append(platformDdl.lowerColumnName(columnName));
    buffer.append(" ").append(platformType);
    if (defaultValue != null) {
      buffer.append(" default ").append(defaultValue);
    }
    if (isNotNull) {
      buffer.append(" not null");
    }
    if (generated != null) {
      buffer.append(" ").append(generated);
    }
  }

  public void disableSystemVersioning(DdlWrite writer, String tableName) {
    disableSystemVersioning(writer, tableName, false);
  }

  public void disableSystemVersioning(DdlWrite writer, String tableName, boolean uniqueStatement) {
    writer.alterTable(tableName, "drop system versioning");
    // CHECKME: Do we need this?
//    apply.append("alter table ").append(tableName).append(" drop system versioning");
//    if (uniqueStatement) {
//      // needed for the DB migration test to prevent the statement from being filtered
//      // out as a duplicate
//      apply.append(" /* ").append(String.valueOf(counter.getAndIncrement())).append(" */");
//    }
//    apply.endOfStatement();
  }

  public void enableSystemVersioning(DdlWrite writer, String tableName, String historyTableName, boolean validated,
                                     boolean uniqueStatement) {
    StringBuilder stmt = writer.alterTable(tableName, "add system versioning history table ").append(historyTableName);
    if (!validated) {
      stmt.append(" not validated");
    }
    // CHECKME: Do we need this?
//    if (uniqueStatement) {
//      // needed for the DB migration test to prevent the statement from being filtered
//      // out as a duplicate
//      apply.append(" /* ").append(String.valueOf(counter.getAndIncrement())).append(" */");
//    }
//    apply.endOfStatement();
  }

  public boolean isSystemVersioningEnabled(String tableName) {
    return !createdHistoryTables.containsKey(tableName);
  }
}
