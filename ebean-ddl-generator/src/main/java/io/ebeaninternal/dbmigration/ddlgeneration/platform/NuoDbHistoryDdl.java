package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.model.MTable;

/**
 * NuoDB history support using DB triggers to maintain a history table.
 */
public class NuoDbHistoryDdl extends DbTriggerBasedHistoryDdl {

  NuoDbHistoryDdl() {
    this.now = "now()";
    this.sysPeriodEndValue = "NEW.sys_period_start";
  }

  @Override
  protected void dropTriggers(DdlBuffer buffer, String baseTable) {

    buffer.append("drop trigger ").append(updateTriggerName(baseTable)).endOfStatement();
    buffer.append("drop trigger ").append(deleteTriggerName(baseTable)).endOfStatement();
  }

  @Override
  protected void createTriggers(DdlBuffer writer, MTable table) {
    // TODO Auto-generated method stub

  }

  //  @Override
  //  protected void createTriggers(DdlWrite writer, MTable table) {
  //
  //    DbTriggerUpdate update = createDbTriggerUpdate(writer, table);
  //
  //    addBeforeUpdate(updateTriggerName(update.getBaseTable()), update);
  //    addBeforeDelete(deleteTriggerName(update.getBaseTable()), update);
  //  }
  //
  //  @Override
  //  protected void updateHistoryTriggers(DbTriggerUpdate update) {
  //
  //    recreateHistoryView(update);
  //
  //    DdlBuffer buffer = update.historyTriggerBuffer();
  //    String baseTable = update.getBaseTable();
  //
  //    dropTriggers(buffer, baseTable);
  //    addBeforeUpdate(updateTriggerName(baseTable), update);
  //    addBeforeDelete(deleteTriggerName(baseTable), update);
  //  }
  //
  //  private void addBeforeUpdate(String triggerName, DbTriggerUpdate update) {
  //
  //    DdlBuffer apply = update.historyTriggerBuffer();
  //    addTriggerStart(triggerName, update, apply, " before update for each row as ");
  //    apply.append("    NEW.sys_period_start = greatest(current_timestamp, date_add(OLD.sys_period_start, interval 1 microsecond))").endOfStatement();
  //    appendInsertIntoHistory(apply, update.getHistoryTable(), update.getColumns());
  //    addEndTrigger(apply);
  //  }
  //
  //  private void addBeforeDelete(String triggerName, DbTriggerUpdate update) {
  //
  //    DdlBuffer apply = update.historyTriggerBuffer();
  //    addTriggerStart(triggerName, update, apply, " before delete for each row as");
  //    appendInsertIntoHistory(apply, update.getHistoryTable(), update.getColumns());
  //    addEndTrigger(apply);
  //  }
  //
  //  private void addTriggerStart(String triggerName, DbTriggerUpdate update, DdlBuffer apply, String s) {
  //    apply
  //      .append("delimiter $$").newLine()
  //      .append("create or replace trigger ").append(triggerName).append(" for ").append(update.getBaseTable())
  //      .append(s).newLine();
  //  }

  private void addEndTrigger(DdlBuffer apply) {
    apply.append("end_trigger")
      .endOfStatement()
      .append("$$").newLine()
      .newLine();
  }

}
