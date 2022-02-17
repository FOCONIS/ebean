package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebean.config.PropertiesWrapper;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AddColumn;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.dbmigration.migration.Column;
import io.ebeaninternal.dbmigration.migration.DropColumn;
import io.ebeaninternal.dbmigration.model.MTable;

import java.io.IOException;
import java.util.List;

public class HanaTableDdl extends BaseTableDdl {

  private final HanaHistoryDdl historyDdl;
  private final boolean generateUniqueDdl;

  public HanaTableDdl(DatabaseConfig config, PlatformDdl platformDdl) {
    super(config, platformDdl);
    this.historyDdl = (HanaHistoryDdl) platformDdl.historyDdl;
    if (config.getProperties() != null) {
      PropertiesWrapper wrapper = new PropertiesWrapper("ebean", "hana", config.getProperties(), config.getClassLoadConfig());
      this.generateUniqueDdl = wrapper.getBoolean("generateUniqueDdl", false);
    } else {
      this.generateUniqueDdl = false;
    }
  }

  @Override
  protected void alterColumnDefaultValue(DdlWrite write, AlterColumn alter) {
    // done in alterColumnBaseAttributes
  }

  @Override
  public void generate(DdlWrite write, AddColumn addColumn) {
    String tableName = addColumn.getTableName();
    MTable table = write.getTable(tableName);
    if (table == null) {
      super.generate(write, addColumn);
      return;
    }

    boolean manageSystemVersioning = isTrue(table.isWithHistory()) && historyDdl.isSystemVersioningEnabled(tableName);

    if (manageSystemVersioning) {
      historyDdl.disableSystemVersioning(write, table.getName(), this.generateUniqueDdl);
    }

    super.generate(write, addColumn);

    if (manageSystemVersioning) {
      // make same changes to the history table
      String historyTable = historyTable(tableName);
      List<Column> columns = addColumn.getColumn();
      for (Column column : columns) {
        alterTableAddColumn(write, historyTable, column, true, true);
      }

      historyDdl.enableSystemVersioning(write, table.getName(), historyTable, false, this.generateUniqueDdl);
    }
  }

  @Override
  public void generate(DdlWrite write, AlterColumn alterColumn) {
    String tableName = alterColumn.getTableName();
    MTable table = write.getTable(tableName);
    if (table == null) {
      super.generate(write, alterColumn);
      return;
    }

    boolean manageSystemVersioning = isTrue(table.isWithHistory()) && historyDdl.isSystemVersioningEnabled(tableName);

    if (manageSystemVersioning) {
      historyDdl.disableSystemVersioning(write, tableName, this.generateUniqueDdl);
    }

    super.generate(write, alterColumn);

    if (manageSystemVersioning) {
      // make same changes to the history table
      String historyTable = historyTable(tableName);
      if (hasValue(alterColumn.getType()) || hasValue(alterColumn.getDefaultValue()) || alterColumn.isNotnull() != null) {
        AlterColumn alterHistoryColumn = new AlterColumn();
        alterHistoryColumn.setTableName(historyTable);
        alterHistoryColumn.setColumnName(alterColumn.getColumnName());
        alterHistoryColumn.setType(alterColumn.getType());
        alterHistoryColumn.setDefaultValue(alterColumn.getDefaultValue());
        alterHistoryColumn.setNotnull(alterColumn.isNotnull());
        alterHistoryColumn.setCurrentType(alterColumn.getCurrentType());
        alterHistoryColumn.setCurrentDefaultValue(alterColumn.getCurrentDefaultValue());
        alterHistoryColumn.setCurrentNotnull(alterColumn.isCurrentNotnull());
        platformDdl.alterColumnBaseAttributes(write, alterHistoryColumn);

      }

      historyDdl.enableSystemVersioning(write, tableName, historyTable, false, this.generateUniqueDdl);
    }
  }

  @Override
  public void generate(DdlWrite write, DropColumn dropColumn) {
    String tableName = dropColumn.getTableName();
    MTable table = write.getTable(tableName);
    if (table == null) {
      super.generate(write, dropColumn);
      return;
    }

    boolean manageSystemVersioning = isTrue(table.isWithHistory()) && historyDdl.isSystemVersioningEnabled(tableName);

    if (manageSystemVersioning) {
      historyDdl.disableSystemVersioning(write, tableName, this.generateUniqueDdl);
    }

    super.generate(write, dropColumn);

    if (manageSystemVersioning) {
      // also drop from the history table
      String historyTable = historyTable(tableName);
      alterTableDropColumn(write, historyTable, dropColumn.getColumnName());

      historyDdl.enableSystemVersioning(write, tableName, historyTable, false, this.generateUniqueDdl);
    }
  }

}
