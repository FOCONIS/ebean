package io.ebeaninternal.dbmigration.ddlgeneration;

import java.io.IOException;

public interface DdlAlterTable {

  boolean isEmpty();

  void write(Appendable target) throws IOException;

  // DdlAlterTable preAdd(String operation);

  /**
   * Adds an alter table command.
   */
  DdlAlterTable add(String operation);

  DdlAlterTable add(String operation, String columnName);

  DdlAlterTable add(String operation, String columnName, String... suffix);

  DdlAlterTable raw(String string);

  boolean isHistoryHandled();

  DdlAlterTable setHistoryHandled();


}
