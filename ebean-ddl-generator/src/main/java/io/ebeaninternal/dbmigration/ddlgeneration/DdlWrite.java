package io.ebeaninternal.dbmigration.ddlgeneration;

import java.io.IOException;
import java.util.function.Function;

import io.ebeaninternal.dbmigration.model.MTable;

public interface DdlWrite {

  /**
   * Returns DdlOptions.
   */
  DdlOptions getOptions();

  /**
   * Returns MTable.
   */
  MTable getTable(String baseTable);

  // -------------------- Buffers for the apply script ---------------------------

  /**
   * Holds dependent drops
   * <ul>
   * <li>drop index</li>
   * <li>drop foreign keys</li>
   * <li>drop history triggers</li>
   * </ul>
   */
  DdlBuffer dropDependencies();

  /**
   * Holds apply statements:
   * <ul>
   * <li>dbMigration.preAlter/preAdd</li>
   * <li>create table statements (and drop tables für drop-all.ddl)</li>
   * <li>create sequence</li>
   * <li>drop history view</li>
   * <li>usp_ebean_drop_column</li>
   * </ul>
   */
  DdlBuffer apply();
  
  /**
   * Holds all alter table statements per table, but NOT foreign keys.
   */
  DdlAlterTable alterTable(String tableName, Function<String, DdlAlterTable> factory);

  DdlAlterTable alterTable(String tableName);

  /**
   * Holds all statements after alter.
   * <ul>
   * <li>&#64;DbMigration.postAdd/postAlter</li>
   * <li>Table/Column comments</li>
   * <li>drop history table (after disabling history on a particular table></li>
   * <ul>
   */
  DdlBuffer postAlter();

  /**
   * Holds statements for history generation.
   */
  DdlBuffer applyHistoryView();

  /**
   * Holds all index/foreign keys and constraint definitions.
   * @return
   */
  DdlBuffer index();

  /**
   * Holds history triggers.
   */
  DdlBuffer applyHistoryTrigger();

  /**
   * Are the apply buffers empty?
   */
  boolean isApplyEmpty();

  /**
   * Write the apply buffers to <code>target</code>
   */
  void writeApply(Appendable target) throws IOException;

  // -------------------- Buffers for the drop script ---------------------------
  /**
   * Holds the drop index statements for drop-all script.
   */
  DdlBuffer dropAllIndex();

  /**
   * Holds the drop table/sequences/... statements for drop-all script.
   */
  DdlBuffer dropAll();

  /**
   * Holds the drop procedures statements for drop-all script.
   */
  DdlBuffer dropAllProcs();

  /**
   * Write the drop all buffers to <code>target</code>
   */
  void writeDropAll(Appendable target) throws IOException;


}
