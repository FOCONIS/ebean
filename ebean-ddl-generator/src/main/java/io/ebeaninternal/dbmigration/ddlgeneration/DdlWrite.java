package io.ebeaninternal.dbmigration.ddlgeneration;

import java.io.IOException;

import io.ebeaninternal.dbmigration.model.MTable;

public interface DdlWrite {

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
   * Holds all alter table statements, but NOT foreign keys.
   */
  StringBuilder alterTable(String tableName, String Statement);
  
  /**
   * Holds all statements after alter.
   */
  DdlBuffer postAlter();

  DdlBuffer applyHistoryView();

  /**
   * Holds all index/foreign keys and constraint definitions.
   * @return
   */
  DdlBuffer index();
  
  DdlBuffer applyHistoryTrigger();

  DdlOptions getOptions();

  // -------------------- Buffers for the drop script ---------------------------  
  DdlBuffer dropAllIndex();
  
  DdlBuffer dropAll();

  DdlBuffer dropAllProcs();
  
  MTable getTable(String baseTable);

  boolean isApplyEmpty();

  void writeApply(Appendable writer) throws IOException;
  
  void writeDropAll(Appendable writer) throws IOException;

}
