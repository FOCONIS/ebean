package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebean.config.DbConstraintNaming;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlAlterTable;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AddHistoryTable;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.dbmigration.migration.DropHistoryTable;
import io.ebeaninternal.dbmigration.model.MTable;

/**
 * @author Vilmos Nagy
 */
public class SqlServerHistoryDdl implements PlatformHistoryDdl {

  protected DbConstraintNaming constraintNaming;
  protected String historySuffix;
  private String systemPeriodStart;
  private String systemPeriodEnd;
  private PlatformDdl platformDdl;

  @Override
  public void configure(DatabaseConfig config, PlatformDdl platformDdl) {
    this.systemPeriodStart = config.getAsOfSysPeriod() + "From";
    this.systemPeriodEnd = config.getAsOfSysPeriod() + "To";
    this.constraintNaming = config.getConstraintNaming();
    this.platformDdl = platformDdl;

    this.historySuffix = config.getHistoryTableSuffix();
    this.constraintNaming = config.getConstraintNaming();
  }

  @Override
  public void createWithHistory(DdlWrite writer, MTable table) {
    String baseTable = table.getName();
    enableSystemVersioning(writer, baseTable);
  }

  private void enableSystemVersioning(DdlWrite writer, String baseTable) {
    DdlBuffer apply = writer.applyPostAlter();
    apply.append("alter table ").append(quote(baseTable)).newLine()
      .append("    add ").append(systemPeriodStart).append(" datetime2 GENERATED ALWAYS AS ROW START NOT NULL DEFAULT SYSUTCDATETIME(),").newLine()
      .append("        ").append(systemPeriodEnd).append("   datetime2 GENERATED ALWAYS AS ROW END   NOT NULL DEFAULT '9999-12-31T23:59:59.9999999',").newLine()
      .append("period for system_time (").append(systemPeriodStart).append(", ").append(systemPeriodEnd).append(")").endOfStatement();

    apply.append("alter table ").append(baseTable).append(" set (system_versioning = on (history_table=")
      .append(historyTableName(baseTable)).append("))").endOfStatement();

    DdlBuffer drop = writer.dropAll();
    drop.append("IF OBJECT_ID('").append(quote(baseTable)).append("', 'U') IS NOT NULL alter table ")
      .append(quote(baseTable)).append(" set (system_versioning = off)").endOfStatement();
    drop.append("IF OBJECT_ID('").append(historyTableName(baseTable)).append("', 'U') IS NOT NULL drop table ")
      .append(historyTableName(baseTable)).endOfStatement();
  }


  @Override
  public void dropHistoryTable(DdlWrite writer, DropHistoryTable dropHistoryTable) {
    String baseTable = dropHistoryTable.getBaseTable();
    // drop default constraints
    AlterColumn alter = new AlterColumn();
    alter.setTableName(baseTable);
    alter.setDefaultValue(DdlHelp.DROP_DEFAULT);
    alter.setColumnName(systemPeriodStart);
    platformDdl.alterColumn(writer, alter);
    alter.setColumnName(systemPeriodEnd);
    platformDdl.alterColumn(writer, alter);

    // switch of versioning & period - must be done before altering
    DdlBuffer apply = writer.apply();
    apply.append("-- dropping history support for ").append(baseTable).endOfStatement();
    apply.append("alter table ").append(quote(baseTable)).append(" set (system_versioning = off)").endOfStatement();
    apply.append("alter table ").append(quote(baseTable)).append(" drop period for system_time").endOfStatement();
    apply.end();
    // now drop tables & columns, they will go to alter table/post alter buffers
    platformDdl.alterTableDropColumn(writer, baseTable, systemPeriodStart);
    platformDdl.alterTableDropColumn(writer, baseTable, systemPeriodEnd);
    writer.applyPostAlter().appendStatement(platformDdl.dropTable(baseTable + "_history"));
  }

  @Override
  public void addHistoryTable(DdlWrite writer, AddHistoryTable addHistoryTable) {
    String baseTable = addHistoryTable.getBaseTable();
    enableSystemVersioning(writer, baseTable);
  }

  @Override
  public void updateTriggers(DdlWrite writer, String tableName) {
    DdlAlterTable alter = platformDdl.alterTable(writer, tableName);
    writer.getTable(tableName);
    if (!alter.isHistoryHandled()) {
      // SQL Server 2016 does not need triggers
      DdlBuffer apply = writer.apply();
      apply.append("-- alter table ").append(historyTableName(tableName)).append(" set (system_versioning = off (history_table=")
        .append(historyTableName(tableName)).append("))").endOfStatement();
      apply.append("-- history migration goes here").newLine();
      apply.append("-- alter table ").append(historyTableName(tableName)).append(" set (system_versioning = on (history_table=")
        .append(historyTableName(tableName)).append("))").endOfStatement();
    }
    alter.setHistoryHandled();
  }

  protected String quote(String baseTable) {
    return platformDdl.quote(baseTable);
  }
  protected String normalise(String tableName) {
    return constraintNaming.normaliseTable(tableName);
  }

  protected String historyTableName(String baseTableName) {
    String baseTable = normalise(baseTableName);
    if (baseTable.indexOf('.') == -1) {
      // history must contain schema, add the default schema if none was specified
      baseTable = "dbo." + baseTable;
    }
    return baseTable + historySuffix;
  }

}
