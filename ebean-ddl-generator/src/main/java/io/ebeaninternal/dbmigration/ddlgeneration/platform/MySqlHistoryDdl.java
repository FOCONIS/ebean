package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
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
  protected void createTriggers(DdlBuffer apply, MTable table) {

    addBeforeUpdate(apply, updateTriggerName(table.getName()), table);
    addBeforeDelete(apply, deleteTriggerName(table.getName()), table);
  }

  private void addBeforeUpdate(DdlBuffer apply, String triggerName, MTable table) {

    apply.append("delimiter $$").newLine().append("create trigger ").append(triggerName).append(" before update on ")
        .append(table.getName()).append(" for each row begin").newLine();
    appendInsertIntoHistory(apply, table.getName(), table.allHistoryColumns(true));
    apply.append("    set NEW.").append(sysPeriod).append("_start = now(6)").endOfStatement().append("end$$").newLine();
  }

  private void addBeforeDelete(DdlBuffer apply, String triggerName, MTable table) {

    apply.append("delimiter $$").newLine().append("create trigger ").append(triggerName).append(" before delete on ")
        .append(table.getName()).append(" for each row begin").newLine();
    appendInsertIntoHistory(apply, table.getName(), table.allHistoryColumns(true));
    apply.append("end$$").newLine();
  }

}
