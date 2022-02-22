package io.ebeaninternal.dbmigration.ddlgeneration;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import io.ebeaninternal.dbmigration.ddlgeneration.platform.BaseDdlBuffer;
import io.ebeaninternal.dbmigration.model.MConfiguration;
import io.ebeaninternal.dbmigration.model.MTable;
import io.ebeaninternal.dbmigration.model.ModelContainer;

/**
 * Write context holding the buffers for both apply and rollback DDL.
 */
public class BaseDdlWrite implements DdlWrite {

  private final ModelContainer currentModel;

  private final DdlBuffer applyDropDependencies = new BaseDdlBuffer();

  
  private final DdlBuffer apply = new BaseDdlBuffer();

  private final Map<String, DdlAlterTable> alterTables = new TreeMap<>();

  private final DdlBuffer postAlter = new BaseDdlBuffer();

  private final DdlBuffer index = new BaseDdlBuffer();

  private final DdlBuffer applyHistoryView = new BaseDdlBuffer();

  private final DdlBuffer applyHistoryTrigger = new BaseDdlBuffer();

  
  private final DdlBuffer dropAllIndex = new BaseDdlBuffer();
  
  private final DdlBuffer dropAll = new BaseDdlBuffer();
  
  private final DdlBuffer dropAllProcs = new BaseDdlBuffer();

  
  private final DdlOptions options;
  

  /**
   * Create without any configuration or current model (no history support).
   */
  public BaseDdlWrite() {
    this(new MConfiguration(), new ModelContainer(), new DdlOptions());
  }

  /**
   * Create with a configuration.
   */
  public BaseDdlWrite(MConfiguration configuration, ModelContainer currentModel, DdlOptions options) {
    this.currentModel = currentModel;
    this.options = options;
  }
  
  /**
   * Return the DDL options.
   */
  public DdlOptions getOptions() {
    return options;
  }

  /**
   * Return the Table information from the current model.
   * <p>
   * This is typically required for the history support (used to determine the list of columns
   * included in the history when creating or recreating the associated trigger/stored procedure).
   * </p>
   */
  public MTable getTable(String tableName) {
    return currentModel.getTable(tableName);
  }

  /**
   * Return true if the apply buffers are all empty.
   */
  public boolean isApplyEmpty() {
    return apply.getBuffer().isEmpty()
      && index.getBuffer().isEmpty()
      && applyHistoryView.getBuffer().isEmpty()
      && applyHistoryTrigger.getBuffer().isEmpty()
      && applyDropDependencies.getBuffer().isEmpty()
      && alterTablesEmpty();
  }

  private boolean alterTablesEmpty() {
    for (DdlAlterTable alterTable : alterTables.values()) {
     if (!alterTable.isEmpty()) {
       return false;
     }
   }
   return true;
  }

  /**
   * Return the buffer that POST ALTER is written to.
   */
  public DdlBuffer postAlter() {
    return postAlter;
  }
  

  @Override
  public DdlAlterTable alterTable(String tableName, Function<String, DdlAlterTable> factory) {
    return alterTables.computeIfAbsent(tableName, factory);
  }

  @Override
  public DdlAlterTable alterTable(String tableName) {
    return alterTables.get(tableName);
  }

  /**
   * Return the buffer that APPLY DDL is written to.
   */
  public DdlBuffer apply() {
    return apply;
  }

  /**
   * Return the buffer that executes early to drop dependencies like views etc.
   */
  public DdlBuffer dropDependencies() {
    return applyDropDependencies;
  }

  @Override
  public DdlBuffer index() {
    return index;
  }

  /**
   * Return the buffer that apply history-view DDL is written to.
   */
  public DdlBuffer applyHistoryView() {
    return applyHistoryView;
  }

  /**
   * Return the buffer that apply history-trigger DDL is written to.
   */
  public DdlBuffer applyHistoryTrigger() {
    return applyHistoryTrigger;
  }
  
  public DdlBuffer dropAllIndex() {
    return dropAllIndex;
  }

  public DdlBuffer dropAll() {
    return dropAll;
  }

  public DdlBuffer dropAllProcs() {
    return dropAllProcs;
  }
  /**
   * Writes the apply ddl to the target.
   */
  public void writeApply(Appendable target) throws IOException {
    if (!applyDropDependencies.isEmpty()) {
      target.append("-- drop dependencies\n");
      target.append(applyDropDependencies.getBuffer());
    }
    if (!apply.isEmpty()) {
      target.append("-- apply changes\n");
      target.append(apply.getBuffer());
    }
    if (!alterTables.isEmpty()) {
      target.append("-- altering tables\n");
      for (DdlAlterTable alterTable : alterTables.values()) {
        alterTable.write(target);
      }
    }
    if (!postAlter.isEmpty()) {
      target.append("-- post alter\n");
      target.append(postAlter.getBuffer());
    }
    if (!index.isEmpty()) {
      target.append("-- indices/constraints\n");
      target.append(index.getBuffer());
    }
    if (!applyHistoryView.isEmpty()) {
      target.append("-- apply history view\n");
      target.append(applyHistoryView.getBuffer());
    }
    if (!applyHistoryTrigger.isEmpty()) {
      target.append("-- apply history trigger\n");
      target.append(applyHistoryTrigger.getBuffer());
    }
  }
  
  /**
   * Writes the drop all ddl to the target.
   */
  public void writeDropAll(Appendable target) throws IOException {
    if (!dropAllIndex.isEmpty()) { 
      target.append("-- drop all indices\n");
      target.append(dropAllIndex.getBuffer());
    }
    if (!dropAll.isEmpty()) { 
      target.append("-- drop all\n");
      target.append(dropAll.getBuffer());
    }
    if (!dropAllProcs.isEmpty()) { 
      target.append("-- drop all procs\n");
      target.append(dropAllProcs.getBuffer());
    }
  }
  
  /**
   * Returns all create statements. Mainly used for unit-tests
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    try {
      writeDropAll(sb);
      writeApply(sb);
    } catch (IOException e) {
      // can not happen
    }
    return sb.toString();
  }


}
