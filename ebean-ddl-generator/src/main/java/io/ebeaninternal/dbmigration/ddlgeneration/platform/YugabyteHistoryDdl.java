package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.model.MTable;

public class YugabyteHistoryDdl extends PostgresHistoryDdl {

  @Override
  protected void createHistoryTable(DdlWrite writer, MTable table) {
    createHistoryTableAs(writer.apply(), table);
    writeColumnDefinition(writer.apply(), sysPeriod, "tstzrange");
    writer.apply().newLine().append(")").endOfStatement();
  }
}
