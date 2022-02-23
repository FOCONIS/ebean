package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.util.StringHelper;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.dbmigration.migration.Column;

import java.util.Collection;

/**
 * MySql specific DDL.
 */
public class MySqlDdl extends PlatformDdl {

  // check constraint support is disabled by default. See https://groups.google.com/forum/#!topic/ebean/luFN-2xBkUw
  // this flag is for compatibility. Use it with care.
  private static final boolean USE_CHECK_CONSTRAINT = Boolean.getBoolean("ebean.mysql.useCheckConstraint");

  private final boolean useMigrationStoredProcedures;

  public MySqlDdl(DatabasePlatform platform) {
    super(platform);
    this.alterColumn = "modify";
    this.dropUniqueConstraint = "drop index";
    this.historyDdl = new MySqlHistoryDdl();
    this.inlineComments = true;
    this.useMigrationStoredProcedures = platform.isUseMigrationStoredProcedures();
  }

  /**
   * Return the drop index statement.
   */
  @Override
  public void dropIndex(DdlBuffer buffer, String indexName, String tableName) {
    buffer.append("drop index ").append(maxConstraintName(indexName)).append(" on ").append(tableName).endOfStatement();
  }

  @Override
  public void alterTableDropColumn(final DdlWrite writer, final String tableName, final String columnName,
      boolean onHistoryTable) {
    if (this.useMigrationStoredProcedures) {
      alterTable(writer, tableName).raw("CALL usp_ebean_drop_column('" + tableName + "', '" + columnName + "')");
    } else {
      super.alterTableDropColumn(writer, tableName, columnName, onHistoryTable);
    }
  }

  /**
   * Return the drop foreign key clause.
   */

  @Override
  public void alterTableDropForeignKey(DdlBuffer buffer, String tableName, String fkName) {
    buffer.append("alter table ").append(tableName).append(" drop foreign key ").append(maxConstraintName(fkName))
      .endOfStatement();
  }

  @Override
  public String createCheckConstraint(String ckName, String checkConstraint) {
    if (USE_CHECK_CONSTRAINT) {
      return super.createCheckConstraint(ckName, checkConstraint);
    } else {
      return null;
    }
  }

  @Override
  public void alterTableAddCheckConstraint(DdlWrite writer, String tableName, String checkConstraintName,
      String checkConstraint) {
    if (USE_CHECK_CONSTRAINT) {
      super.alterTableAddCheckConstraint(writer, tableName, checkConstraintName, checkConstraint);
    }
  }

  @Override
  public void alterTableDropConstraint(DdlBuffer buffer, String tableName, String constraintName) {
    // drop constraint not supported in MySQL 5.7 and 8.0 but starting with MariaDB
    // 10.2.1 CHECK is evaluated
    if (USE_CHECK_CONSTRAINT) {
      // statement for MySQL >= 8.0.16
      buffer.append("/*!80016 alter table ").append(tableName)
        .append(" drop check ").append(maxConstraintName(constraintName)).append(" */")
        .endOfStatement();
      // statement for MariaDB >= 10.2.1
      buffer.append("/*M!100201  alter table ").append(tableName)
        .append(" drop constraint if exists ").append(maxConstraintName(constraintName)).append(" */")
        .endOfStatement();
    }
  }

  @Override
  public void alterColumnType(DdlWrite writer, String tableName, String columnName, String type,
      boolean onHistoryTable) {
    // can't alter itself - done in alterColumnBaseAttributes()
  }

  @Override
  public void alterColumnNotnull(DdlWrite writer, String tableName, String columnName, boolean notnull) {
    // can't alter itself - done in alterColumnBaseAttributes()
  }

  @Override
  public void alterColumnDefaultValue(DdlWrite writer, String tableName, String columnName, String defaultValue) {
    // can't alter itself - done in alterColumnBaseAttributes()
  }

  @Override
  public void alterColumnBaseAttributes(DdlWrite writer, AlterColumn alter, boolean onHistoryTable) {
    String tableName = alter.getTableName();
    String columnName = alter.getColumnName();
    String defaultValue = alter.getDefaultValue();
    if (alter.getType() == null && alter.isNotnull() == null) {
      // No type change or notNull change
      // handle defaultValue change
      if (hasValue(defaultValue)) {
        String suffix = DdlHelp.isDropDefault(defaultValue) ? columnDropDefault
            : columnSetDefault + " " + convertDefaultValue(defaultValue);
        alterTable(writer, tableName).add("alter", columnName, suffix);
      }
      return;
    } else {
      String type = alter.getType() != null ? alter.getType() : alter.getCurrentType();
      type = convert(type);
      StringBuilder sb = new StringBuilder();
      sb.append(type);

      boolean notnull = (alter.isNotnull() != null) ? alter.isNotnull() : Boolean.TRUE.equals(alter.isCurrentNotnull());
      if (notnull) {
        sb.append(" not null");
      }
      if (hasValue(defaultValue) && !DdlHelp.isDropDefault(defaultValue)) {
        sb.append(" default ").append(convertDefaultValue(defaultValue));
      }
      alterTable(writer, tableName).add("modify", columnName, sb.toString());
    }
  }

  @Override
  protected void writeColumnDefinition(DdlBuffer buffer, Column column, DdlIdentity identity) {
    super.writeColumnDefinition(buffer, column, identity);
    String comment = column.getComment();
    if (!StringHelper.isNull(comment)) {
      // in mysql 5.5 column comment save in information_schema.COLUMNS.COLUMN_COMMENT(VARCHAR 1024)
      if (comment.length() > 500) {
        comment = comment.substring(0, 500);
      }
      buffer.append(String.format(" comment '%s'", comment));
    }
  }

  @Override
  public void inlineTableComment(DdlBuffer apply, String tableComment) {
    if (tableComment.length() > 1000) {
      tableComment = tableComment.substring(0, 1000);
    }
    apply.append(" comment='").append(tableComment).append("'");
  }

  /**
   * Add table comment as a separate statement (from the create table statement).
   */
  @Override
  public void addTableComment(DdlWrite writer, String tableName, String tableComment) {
    if (DdlHelp.isDropComment(tableComment)) {
      tableComment = "";
    }
    writer.postAlter().append(String.format("alter table %s comment = '%s'", tableName, tableComment)).endOfStatement();
  }

  @Override
  public void addColumnComment(DdlWrite writer, String table, String column, String comment) {
    // alter comment currently not supported as it requires to repeat whole column definition
  }


  /**
   * Locks all tables for triggers that have to be updated.
   */
  @Override
  public void lockTables(DdlBuffer buffer, Collection<String> tables) {
    if (!tables.isEmpty()) {
      buffer.append("lock tables ");
      int i = 0;
      for (String table : tables) {
        if (i > 0) {
          buffer.append(", ");
        }
        buffer.append(table).append(" write");
        i++;
      }
      buffer.endOfStatement();
    }
  }

  /**
   * Unlocks all tables for triggers that have to be updated.
   */
  @Override
  public void unlockTables(DdlBuffer buffer, Collection<String> tables) {
    buffer.append("unlock tables").endOfStatement();
  }

}
