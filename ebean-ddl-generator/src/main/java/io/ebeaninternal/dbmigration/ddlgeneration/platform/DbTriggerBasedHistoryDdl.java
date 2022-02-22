package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebean.config.DbConstraintNaming;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlAlterTable;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AddHistoryTable;
import io.ebeaninternal.dbmigration.migration.DropHistoryTable;
import io.ebeaninternal.dbmigration.model.MColumn;
import io.ebeaninternal.dbmigration.model.MTable;

import java.util.List;

/**
 * Uses DB triggers to maintain a history table.
 */
public abstract class DbTriggerBasedHistoryDdl implements PlatformHistoryDdl {

  protected DbConstraintNaming constraintNaming;

  protected PlatformDdl platformDdl;

  protected String sysPeriod;
  protected String sysPeriodStart;
  protected String sysPeriodEnd;

  protected String viewSuffix;
  protected String historySuffix;

  protected String sysPeriodType = "datetime(6)";
  protected String now = "now(6)";
  protected String sysPeriodEndValue = "now(6)";

  DbTriggerBasedHistoryDdl() {
  }

  @Override
  public void configure(DatabaseConfig config, PlatformDdl platformDdl) {
    this.platformDdl = platformDdl;
    this.sysPeriod = config.getAsOfSysPeriod();
    this.viewSuffix = config.getAsOfViewSuffix();
    this.historySuffix = config.getHistoryTableSuffix();
    this.constraintNaming = config.getConstraintNaming();

    this.sysPeriodStart = sysPeriod + "_start";
    this.sysPeriodEnd = sysPeriod + "_end";
  }

  @Override
  public void dropHistoryTable(DdlWrite writer, DropHistoryTable dropHistoryTable) {

    String baseTable = dropHistoryTable.getBaseTable();

    // drop in appropriate order
    dropTriggers(writer.dropDependencies(), baseTable);
    dropWithHistoryView(writer.dropDependencies(), baseTable);
    dropHistoryTable(writer.dropDependencies(), baseTable);

    dropSysPeriodColumns(writer, baseTable);
  }

  @Override
  public void addHistoryTable(DdlWrite writer, AddHistoryTable addHistoryTable) {

    String baseTable = addHistoryTable.getBaseTable();
    MTable table = writer.getTable(baseTable);
    if (table == null) {
      throw new IllegalStateException("MTable " + baseTable + " not found in writer? (required for history DDL)");
    }

    createWithHistory(writer, table);
  }

  @Override
  public void createWithHistory(DdlWrite writer, MTable table) {

    String baseTable = table.getName();
    createHistoryTable(writer, table);

    addSysPeriodColumns(writer, baseTable, table.getWhenCreatedColumn());

    createTriggers(writer.postAlter(), table);
    createWithHistoryView(writer.postAlter(), table.getName());

    // drop all scripts
    dropTriggers(writer.dropAll(), baseTable);
    dropWithHistoryView(writer.dropAll(), baseTable);
    dropHistoryTable(writer.dropAll(), baseTable);
    // no need to dropSysPeriodColumns as whole table will be deleted soon
  }

  @Override
  public void regenerateHistory(DdlWrite writer, String tableName) {
    MTable table = writer.getTable(tableName);
    if (table != null && table.isWithHistory()) {
      DdlAlterTable alter = writer.alterTable(tableName);
      if (alter != null && !alter.isHistoryHandled()) {

        dropTriggers(writer.apply(), tableName);
        writer.apply().append("drop view ").append(tableName).append(viewSuffix).endOfStatement();
        createWithHistoryView(writer.postAlter(), tableName);
        createTriggers(writer.postAlter(), table);

        alter.setHistoryHandled();
      }
    }
  }

  protected abstract void createTriggers(DdlBuffer writer, MTable table);

  protected abstract void dropTriggers(DdlBuffer buffer, String baseTable);

  protected String normalise(String tableName) {
    return constraintNaming.normaliseTable(tableName);
  }

  protected String historyTableName(String baseTableName) {
    return baseTableName + historySuffix;
  }

  protected String procedureName(String baseTableName) {
    return baseTableName + "_history_version";
  }

  protected String triggerName(String baseTableName) {
    return normalise(baseTableName) + "_history_upd";
  }

  protected String updateTriggerName(String baseTableName) {
    return normalise(baseTableName) + "_history_upd";
  }

  protected String deleteTriggerName(String baseTableName) {
    return normalise(baseTableName) + "_history_del";
  }

  protected void addSysPeriodColumns(DdlWrite writer, String baseTableName, String whenCreatedColumn) {

    platformDdl.alterTable(writer, baseTableName) // add default history columns
        .add("add column", sysPeriodStart, sysPeriodType, "default", now)
        .add("add column", sysPeriodEnd, sysPeriodType, "default", now).setHistoryHandled();
    if (whenCreatedColumn != null) {
      writer.postAlter().append("update ").append(baseTableName).append(" set ").append(sysPeriodStart).append(" = ")
          .append(whenCreatedColumn).endOfStatement();
    }
  }

  protected void createHistoryTable(DdlWrite writer, MTable table) {
    createHistoryTableAs(writer.apply(), table);
    createHistoryTableWithPeriod(writer.apply());
  }

  protected void createHistoryTableAs(DdlBuffer apply, MTable table) {
    apply.append(platformDdl.getCreateTableCommandPrefix()).append(" ").append(table.getName()).append(historySuffix).append("(").newLine();
    for (MColumn column : table.allColumns()) {
      if (!column.isDraftOnly()) {
        writeColumnDefinition(apply, column.getName(), column.getType());
        apply.append(",").newLine();
      }
    }
  }

  protected void createHistoryTableWithPeriod(DdlBuffer apply) {
    writeColumnDefinition(apply, sysPeriodStart, sysPeriodType);
    apply.append(",").newLine();
    writeColumnDefinition(apply, sysPeriodEnd, sysPeriodType);
    apply.newLine().append(")").endOfStatement();
  }

  protected void dropHistoryTable(DdlBuffer apply, String baseTableName) {
    apply.append("drop table ").append(baseTableName).append(historySuffix).endOfStatement().end();
  }

  /**
   * Write the column definition to the create table statement.
   */
  protected void writeColumnDefinition(DdlBuffer buffer, String columnName, String type) {

    String platformType = platformDdl.convert(type);
    buffer.append("  ");
    buffer.append(platformDdl.lowerColumnName(columnName), 29);
    buffer.append(platformType);
  }

  protected void createWithHistoryView(DdlBuffer apply, String baseTableName) {

    apply
      .append("create view ").append(baseTableName).append(viewSuffix)
      .append(" as select * from ").append(baseTableName)
      .append(" union all select * from ").append(baseTableName).append(historySuffix)
      .endOfStatement().end();
  }

  protected void dropWithHistoryView(DdlBuffer apply, String baseTableName) {
    apply.append("drop view ").append(baseTableName).append(viewSuffix).endOfStatement();
  }

  protected void dropSysPeriodColumns(DdlWrite writer, String baseTableName) {
    platformDdl.alterTableDropColumn(writer, baseTableName, sysPeriodStart, false);
    platformDdl.alterTableDropColumn(writer, baseTableName, sysPeriodEnd, false);
  }

  protected void appendInsertIntoHistory(DdlBuffer buffer, String baseTable, List<String> columns) {

    buffer.append("    insert into ").append(baseTable).append(historySuffix);
    buffer.append(" (").append(sysPeriodStart).append(",").append(sysPeriodEnd).append(",");
    appendColumnNames(buffer, columns, "");
    buffer.append(") values (OLD.").append(sysPeriodStart).append(", ").append(sysPeriodEndValue).append(",");
    appendColumnNames(buffer, columns, "OLD.");
    buffer.append(");").newLine();
  }

  void appendColumnNames(DdlBuffer buffer, List<String> columns, String columnPrefix) {
    for (int i = 0; i < columns.size(); i++) {
      if (i > 0) {
        buffer.append(", ");
      }
      buffer.append(columnPrefix);
      buffer.append(columns.get(i));
    }
  }

  /**
   * Return the column names included in history for the apply script.
   * <p>
   * Note that dropped columns are actually still included at this point as they are going
   * to be removed from the history handling when the drop script runs that also deletes
   * the column.
   * </p>
   */
  List<String> columnNamesForApply(MTable table) {
    return table.allHistoryColumns(true);
  }

  @Override
  public boolean alterHistoryTables() {
    return true;
  }
}
