package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import io.ebeaninternal.dbmigration.ddlgeneration.DdlAlterTable;

/**
 * Contains alter statements per table.
 * 
 * @author Roland Praml, FOCONIS AG
 */
public class BaseAlterTableWrite implements DdlAlterTable {

  public static class AlterCmd {
    // the command (e.g. "alter", "modify"
    public final String operation;
    // the affected column (note: each column can only be altered once on MariaDB)
    public final String column;

    // the alter definition
    public final String suffix;

    AlterCmd(String operation, String column, String suffix) {
      this.operation = operation;
      this.column = column;
      this.suffix = suffix;
    }
  }

  private final String tableName;
  // private final DdlAlterMerger merger;
  private Set<String> pre = new LinkedHashSet<>();
  protected List<AlterCmd> cmds = new ArrayList<>();
  private Set<String> post = new LinkedHashSet<>();

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
      cmds.add(new AlterCmd(operation, column, null));

    } else if (suffix.length == 1) {
      assert !suffix[0].startsWith(" ") &&  !suffix[0].endsWith(" ") : "Space should be avoided" + suffix[0];
      cmds.add(new AlterCmd(operation, column, suffix[0]));

    } else {
      StringJoiner sj = new StringJoiner(" ");
      for (String s: suffix) {
        if (s == null || s.isEmpty())
          continue;
        assert !s.startsWith(" ") &&  !s.endsWith(" ") : "Space should be avoided" + s; 
        sj.add(s);
      }
      cmds.add(new AlterCmd(operation, column, sj.toString()));

    }

    return this;
  }

  @Override
  public DdlAlterTable add(String operation, String column) {
    cmds.add(new AlterCmd(operation, column, null));
    return this;
  }

  @Override
  public DdlAlterTable add(String operation) {
    return add(operation, null);
  }

  public boolean isEmpty() {
    return pre.isEmpty() && post.isEmpty() && cmds.isEmpty();
  }

  /**
   * Writes the DDL to <code>target</code>.
   */
  public void write(Appendable target) throws IOException {
    if (!isEmpty()) {
      for (String stmt : pre) {
        target.append(stmt).append(";\n");
      }
      for (AlterCmd cmd : cmds) {
        target.append("alter table ").append(tableName).append(' ').append(cmd.operation);
        if (cmd.column != null) {
          target.append(' ').append(cmd.column);
        }
        if (cmd.suffix != null) {
          target.append(' ').append(cmd.suffix);
        }
        target.append(";\n");
      }
      for (String stmt : post) {
        target.append(stmt).append(";\n");
      }
    }
  }

  /**
   * Commands executed BEFORE altering a certain table.
   */
  public Set<String> pre() {
    return pre;
  }

  /**
   * Commands executed AFTER altering a certain table.
   */
  public Set<String> post() {
    return post;
  }

  public List<AlterCmd> cmds() {
    return cmds;
  }

  @Override
  public DdlAlterTable preAdd(String operation) {
    pre.add(operation);
    return this;
  }

  @Override
  public DdlAlterTable postAdd(String operation) {
    post.add(operation);
    return this;
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
