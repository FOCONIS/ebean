package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.model.MTable;

/**
 * MySql history support using DB triggers to maintain a history table.
 */
public class MySqlHistoryDdl extends DbTriggerBasedHistoryDdl {

  MySqlHistoryDdl() {
  }

  @Override
  protected void dropTriggers(DdlBuffer buffer, String baseTable) {
    buffer.append("drop trigger ").append(updateTriggerName(baseTable)).endOfStatement();
    buffer.append("drop trigger ").append(deleteTriggerName(baseTable)).endOfStatement();
  }


  @Override
  protected void createTriggers(DdlBuffer buffer, MTable table) {

    buffer.append("lock tables ").append(table.getName()).append(" write").endOfStatement();
    addBeforeUpdate(buffer, updateTriggerName(table.getName()), table);
    addBeforeDelete(buffer, deleteTriggerName(table.getName()), table);
    buffer.appendStatement("unlock tables");

  }

  private void addBeforeUpdate(DdlBuffer apply, String triggerName, MTable table) {

    apply
      .append("delimiter $$").newLine()
      .append("create trigger ").append(triggerName).append(" before update on ").append(table.getName())
      .append(" for each row begin").newLine();
    appendInsertIntoHistory(apply, table.getName(), columnNamesForApply(table));
    apply
      .append("    set NEW.").append(sysPeriod).append("_start = now(6)").endOfStatement()
      .append("end$$").newLine();
  }

  private void addBeforeDelete(DdlBuffer apply, String triggerName, MTable table) {

    apply
      .append("delimiter $$").newLine()
      .append("create trigger ").append(triggerName).append(" before delete on ").append(table.getName())
      .append(" for each row begin").newLine();
    appendInsertIntoHistory(apply, table.getName(), columnNamesForApply(table));
    apply.append("end$$").newLine();
  }

}
