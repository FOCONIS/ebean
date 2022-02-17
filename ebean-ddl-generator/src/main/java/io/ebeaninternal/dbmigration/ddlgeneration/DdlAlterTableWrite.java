package io.ebeaninternal.dbmigration.ddlgeneration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains alter statements per table.
 * 
 * @author Roland Praml, FOCONIS AG
 */
class DdlAlterTableWrite {
  private final String tableName;
  private final List<StringBuilder> alter = new ArrayList<>();
  private String reorg;

  public DdlAlterTableWrite(String tableName) {
    this.tableName = tableName;
  }
  
  public String tableName() {
    return tableName;
  }
  
  /**
   * Adds a statement. The statement is prefixed with "alter table TABLENAME" and may be batched, if platform supports this.
   * The returned StringBuilder can be used, to complete the statement
   */
  public StringBuilder alter(String statement) {
    StringBuilder sb = new StringBuilder(statement);
    alter.add(sb);
    return sb;
  }

  public boolean isEmpty() {
    return alter.isEmpty();
  }

  /**
   * Set the reorg command.
   */
  public void setReorg(String reorg) {
    this.reorg = reorg;
  }

  /**
   * Writes the DDL to <code>target</code>.
   */
  public void write(Appendable target, boolean merge) throws IOException {
    if (!isEmpty()) {
      for (int i = 0; i < alter.size(); i++) {
        if (merge) {
          if (i == 0) {
            target.append("alter table ").append(tableName).append(' ');
          }
          target.append(alter.get(i));
          if (i < alter.size() - 1) {
            target.append(",\n   ");
          } else {
            target.append(";\n");
          }
        } else {
          target.append("alter table ").append(tableName).append(' ');
          target.append(alter.get(i)).append(";\n");
        }
      }
      if (reorg != null) {
        target.append(reorg);
      }
    }
  }
}
