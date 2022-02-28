package io.ebeaninternal.dbmigration.ddlgeneration;

import java.io.IOException;

public interface DdlAlterTable {

  boolean isEmpty();

  void write(Appendable target) throws IOException;

  /**
   * Adds an alter table command.
   */
  DdlBuffer add(String operation);

  DdlBuffer add(String operation, String columnName);

  DdlBuffer raw(String string);

  boolean isHistoryHandled();

  void setHistoryHandled();


}
