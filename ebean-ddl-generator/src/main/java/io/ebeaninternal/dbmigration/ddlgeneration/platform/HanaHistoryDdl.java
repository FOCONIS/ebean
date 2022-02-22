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

public class HanaHistoryDdl implements PlatformHistoryDdl {

  private String systemPeriodStart;
  private String systemPeriodEnd;
  private AbstractHanaDdl platformDdl;
  private String historySuffix;
  private Map<String, String> createdHistoryTables = new ConcurrentHashMap<>();

  @Override
  public void configure(DatabaseConfig config, PlatformDdl platformDdl) {
    this.systemPeriodStart = config.getAsOfSysPeriod() + "_start";
    this.systemPeriodEnd = config.getAsOfSysPeriod() + "_end";
    this.platformDdl = (AbstractHanaDdl) platformDdl;
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
    platformDdl.alterTable(writer, tableName)
        .add("add", systemPeriodStart, "TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START")
        .add("add", systemPeriodEnd, "TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END")
        .add("add period for system_time(" + systemPeriodStart + "," + systemPeriodEnd + ")")
        .postAdd(enableSystemVersioning(tableName, true)).setHistoryHandled();

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
    writer.dropDependencies().append(disableSystemVersioning(baseTable)).endOfStatement();
    writer.dropDependencies().append("alter table ").append(baseTable).append(" drop period for system_time")
        .endOfStatement();

    // writer.postAlter().append("alter table ").append(baseTable).append(" drop
    // period for system_time");

    // drop the period columns
    platformDdl.alterTable(writer, baseTable).add("drop", systemPeriodStart).add("drop", systemPeriodEnd);

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

  public String disableSystemVersioning(String tableName) {
    return "alter table " + tableName + " drop system versioning";
  }

  public String enableSystemVersioning(String tableName, boolean validated) {
    return "alter table " + tableName + " add system versioning history table " + tableName + historySuffix
        + (validated ? "" : " not validated");
  }

  @Override
  public boolean alterHistoryTables() {
    return true;
  }
}
