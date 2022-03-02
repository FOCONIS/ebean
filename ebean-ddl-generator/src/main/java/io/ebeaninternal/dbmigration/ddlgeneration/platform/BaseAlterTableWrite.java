package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import io.ebeaninternal.dbmigration.ddlgeneration.DdlAlterTable;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;

/**
 * Contains alter statements per table.
 * 
 * @author Roland Praml, FOCONIS AG
 */
public class BaseAlterTableWrite implements DdlAlterTable {

  protected static class AlterCmd {
    // the command (e.g. "alter", "modify"
    public final String operation;
    // the affected column (note: each column can only be altered once on MariaDB)
    public final String column;

    public final DdlBuffer buffer = new BaseDdlBuffer() {
      @Override
      public DdlBuffer endOfStatement() {
        throw new UnsupportedOperationException();
      };
    };

    AlterCmd(String operation, String column) {
      this.operation = operation;
      this.column = column;
    }
  }

  private final String tableName;
  // private final DdlAlterMerger merger;
  protected List<AlterCmd> cmds = new ArrayList<>();

  private boolean historyHandled;

  public BaseAlterTableWrite(String tableName) {
    this.tableName = tableName;
  }
  

  public String tableName() {
    return tableName;
  }
  
  /**
   * Adds a statement. The statement is prefixed with "alter table TABLENAME" and may be batched, if platform supports this.
   * The returned StringBuilder can be used, to complete the statement
   */
  @Override
  public DdlBuffer add(String operation, String column) {
    AlterCmd cmd = new AlterCmd(operation, column);
    cmds.add(cmd);
    return cmd.buffer;
  }


  @Override
  public DdlBuffer add(String operation) {
    return add(operation, null);
  }

  @Override
  public DdlBuffer raw(String sql) {
    AlterCmd cmd = new AlterCmd("$RAW", null);
    cmds.add(cmd);
    return cmd.buffer.append(sql);
  }

  @Override
  public boolean isEmpty() {
    return cmds.isEmpty();
  }

  /**
   * Writes the DDL to <code>target</code>.
   */
  @Override
  public void write(Appendable target) throws IOException {
    for (AlterCmd cmd : cmds) {
      if (cmd.operation.equals("$RAW")) {
        // this is a raw command. e.g. an USP call. Must be done in the correct order
        // of all alter commands
        target.append(cmd.buffer.getBuffer());
      } else {
        target.append("alter table ").append(tableName).append(' ').append(cmd.operation);
        if (cmd.column != null) {
          target.append(' ').append(cmd.column);
        }
        if (!cmd.buffer.isEmpty()) {
          target.append(' ').append(cmd.buffer.getBuffer());
        }
      }
      target.append(";\n");
    }
  }

  public List<AlterCmd> cmds() {
    return cmds;
  }

  @Override
  public boolean isHistoryHandled() {
    return historyHandled;
  }

  @Override
  public void setHistoryHandled() {
    historyHandled = true;
  }
}
