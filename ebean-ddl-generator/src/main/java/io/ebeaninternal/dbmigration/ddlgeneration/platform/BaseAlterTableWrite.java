package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import io.ebeaninternal.dbmigration.ddlgeneration.DdlAlterTable;

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

    // the alter definition
    public final String suffix;

    public final boolean raw;

    AlterCmd(String operation, String column, String suffix, boolean raw) {
      this.operation = operation;
      this.column = column;
      this.suffix = suffix;
      this.raw = raw;
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
  public BaseAlterTableWrite add(String operation, String column, String... suffix) {
    if (suffix == null || suffix.length == 0) {
      cmds.add(new AlterCmd(operation, column, null, false));

    } else if (suffix.length == 1) {
      assert !suffix[0].startsWith(" ") &&  !suffix[0].endsWith(" ") : "Space should be avoided" + suffix[0];
      cmds.add(new AlterCmd(operation, column, suffix[0], false));

    } else {
      StringJoiner sj = new StringJoiner(" ");
      for (String s: suffix) {
        if (s == null || s.isEmpty())
          continue;
        assert !s.startsWith(" ") &&  !s.endsWith(" ") : "Space should be avoided" + s; 
        sj.add(s);
      }
      cmds.add(new AlterCmd(operation, column, sj.toString(), false));

    }

    return this;
  }

  @Override
  public DdlAlterTable add(String operation, String column) {
    cmds.add(new AlterCmd(operation, column, null, false));
    return this;
  }

  @Override
  public DdlAlterTable add(String operation) {
    return add(operation, null);
  }

  @Override
  public DdlAlterTable raw(String sql) {
    cmds.add(new AlterCmd(sql, null, null, true));
    return this;
  }

  public boolean isEmpty() {
    return cmds.isEmpty();
  }

  /**
   * Writes the DDL to <code>target</code>.
   */
  public void write(Appendable target) throws IOException {
    for (AlterCmd cmd : cmds) {
      if (cmd.raw) {
        target.append(cmd.operation);
      } else {
        target.append("alter table ").append(tableName).append(' ').append(cmd.operation);
        if (cmd.column != null) {
          target.append(' ').append(cmd.column);
        }
        if (cmd.suffix != null) {
          target.append(' ').append(cmd.suffix);
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
  public DdlAlterTable setHistoryHandled() {
    historyHandled = true;
    return this;
  }
}
