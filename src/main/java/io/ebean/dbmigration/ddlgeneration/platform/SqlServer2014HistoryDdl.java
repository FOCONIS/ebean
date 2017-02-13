package io.ebean.dbmigration.ddlgeneration.platform;

import io.ebean.dbmigration.ddlgeneration.DdlBuffer;
import io.ebean.dbmigration.ddlgeneration.DdlWrite;
import io.ebean.dbmigration.model.MTable;

import java.io.IOException;

/**
 * MS SQL Server platform specific DDL.
 */
public class SqlServer2014HistoryDdl extends DbTriggerBasedHistoryDdl {

  public SqlServer2014HistoryDdl() {
    currentTimestamp = "getutcdate()";
    sysPeriodType = "datetime2";
  }

  @Override
  protected void dropTriggers(DdlBuffer buffer, String baseTable) throws IOException {

    buffer.append("drop trigger ").append(updateTriggerName(baseTable)).endOfStatement();
    buffer.append("drop trigger ").append(deleteTriggerName(baseTable)).endOfStatement();
  }

  @Override
  protected void createTriggers(DdlWrite writer, MTable table) throws IOException {

    DbTriggerUpdate update = createDbTriggerUpdate(writer, table);

    addBeforeUpdate(updateTriggerName(update.getBaseTable()), update);
    addBeforeDelete(deleteTriggerName(update.getBaseTable()), update);
  }

  @Override
  protected void updateHistoryTriggers(DbTriggerUpdate update) throws IOException {
//
//    DdlBuffer buffer = update.historyBuffer();
//    String baseTable = update.getBaseTable();
//
//    buffer.append("lock tables ").append(baseTable).append(" write").endOfStatement();
//    dropTriggers(buffer, baseTable);
//    addBeforeUpdate(updateTriggerName(baseTable), update);
//    addBeforeDelete(deleteTriggerName(baseTable), update);
//    buffer.append("unlock tables").endOfStatement();
  }

  
  @Override
  protected void addSysPeriodColumns(DdlBuffer apply, String baseTableName, String whenCreatedColumn) throws IOException {

    apply.append("alter table ").append(baseTableName).append(" add ")
      .append(sysPeriodStart).append(" ").append(sysPeriodType).append(" default ").append(currentTimestamp).endOfStatement();
    apply.append("alter table ").append(baseTableName).append(" add ")
      .append(sysPeriodEnd).append(" ").append(sysPeriodType).endOfStatement();

    if (whenCreatedColumn != null) {
      apply.append("update ").append(baseTableName).append(" set ").append(sysPeriodStart).append(" = ").append(whenCreatedColumn).endOfStatement();
    }
  }
  
  private void addBeforeUpdate(String triggerName, DbTriggerUpdate update) throws IOException {

//    DdlBuffer apply = update.historyBuffer();
//    apply
//      .append("create trigger ").append(triggerName).append(" on ").append(update.getBaseTable())
//      .append("for update " )
//      .append(" for each row begin").newLine();
//    appendInsertIntoHistory(apply, update.getHistoryTable(), update.getColumns());
//    apply
//      .append("    set NEW.").append(sysPeriod).append("_start = now(6)").endOfStatement()
//      .append("end$$").newLine();
  }

  private void addBeforeDelete(String triggerName, DbTriggerUpdate update) throws IOException {

//    DdlBuffer apply = update.historyBuffer();
//    apply
//      .append("delimiter $$").newLine()
//      .append("create trigger ").append(triggerName).append(" before delete on ").append(update.getBaseTable())
//      .append(" for each row begin").newLine();
//    appendInsertIntoHistory(apply, update.getHistoryTable(), update.getColumns());
//    apply.append("end$$").newLine();
  }

}
