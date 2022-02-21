package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.annotation.ConstraintMode;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.AlterColumn;

/**
 * MS SQL Server platform specific DDL.
 */
public class SqlServerDdl extends PlatformDdl {

  public SqlServerDdl(DatabasePlatform platform) {
    super(platform);
    this.identitySuffix = " identity(1,1)";
    this.alterTableIfExists = "";
    this.addColumn = "add";
    this.inlineUniqueWhenNullable = false;
    this.columnSetDefault = "add default";
    this.dropConstraintIfExists = "drop constraint";
    this.historyDdl = new SqlServerHistoryDdl();
  }

  @Override
  protected void appendForeignKeyMode(StringBuilder buffer, String onMode, ConstraintMode mode) {
    if (mode != ConstraintMode.RESTRICT) {
      super.appendForeignKeyMode(buffer, onMode, mode);
    }
  }

  @Override
  public String dropTable(String tableName) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("IF OBJECT_ID('");
    buffer.append(tableName);
    buffer.append("', 'U') IS NOT NULL drop table ");
    buffer.append(tableName);
    return buffer.toString();
  }

  @Override
  public String alterTableDropForeignKey(String tableName, String fkName) {
    int pos = tableName.lastIndexOf('.');
    String objectId = maxConstraintName(fkName);
    if (pos != -1) {
      objectId = tableName.substring(0, pos + 1) + fkName;
    }
    return "IF OBJECT_ID('" + objectId + "', 'F') IS NOT NULL " + super.alterTableDropForeignKey(tableName, fkName);
  }

  @Override
  public String dropSequence(String sequenceName) {
    return "IF OBJECT_ID('" + sequenceName + "', 'SO') IS NOT NULL drop sequence " + sequenceName;
  }

  @Override
  public String dropIndex(String indexName, String tableName, boolean concurrent) {
    return "IF EXISTS (SELECT name FROM sys.indexes WHERE object_id = OBJECT_ID('" + tableName + "','U') AND name = '"
        + maxConstraintName(indexName) + "') drop index " + maxConstraintName(indexName) + " ON " + tableName;
  }
  /**
   * MsSqlServer specific null handling on unique constraints.
   */
  @Override
  public void alterTableAddUniqueConstraint(DdlWrite writer, String tableName, String uqName, String[] columns, String[] nullableColumns) {
    if (nullableColumns == null || nullableColumns.length == 0) {
      super.alterTableAddUniqueConstraint(writer, tableName, uqName, columns, nullableColumns);
      return; 
    }
    if (uqName == null) {
      throw new NullPointerException();
    }
    // issues#233
    
    DdlBuffer buffer = writer.index().append("create unique nonclustered index ")
        .append(uqName).append(" on ").append(tableName).append("(");

    for (int i = 0; i < columns.length; i++) {
      if (i > 0) {
        buffer.append(",");
      }
      buffer.append(columns[i]);
    }
    buffer.append(") where");
    String sep = " ";
    for (String column : nullableColumns) {
      buffer.append(sep).append(column).append(" is not null");
      sep = " and ";
    }
    buffer.end();
  }

  @Override
  public void alterTableDropConstraint(DdlWrite writer, String tableName, String constraintName) {
    writer.index().append("IF (OBJECT_ID('").append(constraintName).append("', 'C') IS NOT NULL) alter table ")
      .append(tableName).append(" drop constraint ").append(constraintName);
  }
  /**
   * Drop a unique constraint from the table (Sometimes this is an index).
   */
  @Override
  public void alterTableDropUniqueConstraint(DdlWrite writer, String tableName, String uniqueConstraintName) {
    writer.index().appendStatement(dropIndex(uniqueConstraintName, tableName));
    writer.index().append("IF (OBJECT_ID('").append(uniqueConstraintName).append("', 'UQ') IS NOT NULL) alter table ")
      .append(tableName).append(" drop constraint ").append(uniqueConstraintName).endOfStatement();
  }
  /**
   * Generate and return the create sequence DDL.
   */
  @Override
  public String createSequence(String sequenceName, DdlIdentity identity) {
    StringBuilder sb = new StringBuilder(80);
    sb.append("create sequence ").append(sequenceName).append(" as bigint");
    final int start = identity.getStart();
    if (start > 1) {
      sb.append(" start with ").append(start);
    } else {
      sb.append(" start with 1");
    }
    final int increment = identity.getIncrement();
    if (increment > 1) {
      sb.append(" increment by ").append(increment);
    }
    final int cache = identity.getCache();
    if (cache > 1) {
      sb.append(" cache ").append(increment);
    }
    sb.append(";");
    return sb.toString();
  }

  @Override
  public void alterColumnDefaultValue(DdlWrite writer, String tableName, String columnName, String defaultValue) {
    // Unfortunately, the SqlServer creates default values with a random name.
    // You can specify a name in DDL, but this does not work in conjunction with
    // temporal tables in certain cases. So we have to delete the constraint with
    // a rather complex statement.
    if (DdlHelp.isDropDefault(defaultValue)) {
      writer.apply().append("EXEC usp_ebean_drop_default_constraint ").append(tableName).append(", ").append(columnName).endOfStatement();
    } else {
      writer.alterTable(tableName, "add default ").append(convertDefaultValue(defaultValue)).append(" for ").append(columnName);
    }
  }

  @Override
  public void alterColumnBaseAttributes(DdlWrite writer, AlterColumn alter) {
    if (alter.getType() == null && alter.isNotnull() == null) {
      // No type change or notNull change
      // defaultValue change already handled in alterColumnDefaultValue
      return;
    }
    String tableName = alter.getTableName();
    String columnName = alter.getColumnName();
    String type = alter.getType() != null ? alter.getType() : alter.getCurrentType();
    type = convert(type);
    boolean notnull = (alter.isNotnull() != null) ? alter.isNotnull() : Boolean.TRUE.equals(alter.isCurrentNotnull());
    String notnullClause = notnull ? " not null" : "";
    writer.alterTable(tableName, alterColumn).append(' ').append(columnName).append(' ').append(type).append(notnullClause);
  }

  @Override
  public void alterColumnType(DdlWrite writer, String tableName, String columnName, String type) {
    // can't alter itself - done in alterColumnBaseAttributes()
  }

  @Override
  public void alterColumnNotnull(DdlWrite writer, String tableName, String columnName, boolean notnull) {

    // can't alter itself - done in alterColumnBaseAttributes()
  }

  /**
   * Add table comment as a separate statement (from the create table statement).
   */
  @Override
  public void addTableComment(DdlWrite writer, String tableName, String tableComment) {

    // do nothing for MS SQL Server (cause it requires stored procedures etc)
  }

  /**
   * Add column comment as a separate statement.
   */
  @Override
  public void addColumnComment(DdlWrite writer, String table, String column, String comment) {

    // do nothing for MS SQL Server (cause it requires stored procedures etc)
  }

  /**
   * It is rather complex to delete a column on SqlServer as there must not exist any references
   * (constraints, default values, indices and foreign keys). That's why we call a user stored procedure here
   */
  @Override
  public void alterTableDropColumn(DdlWrite writer, String tableName, String columnName) {
    writer.apply().append("EXEC usp_ebean_drop_column ").append(tableName).append(", ").append(columnName).endOfStatement();
  }

  /**
   * This writes the multi value datatypes needed for MultiValueBind.
   */
  @Override
  public void generateProlog(DdlWrite writer) {
    super.generateProlog(writer);

    generateTVPDefinitions(writer, "bigint");
    generateTVPDefinitions(writer, "float");
    generateTVPDefinitions(writer, "bit");
    generateTVPDefinitions(writer, "date");
    generateTVPDefinitions(writer, "time");
    //generateTVPDefinitions(write, "datetime2");
    generateTVPDefinitions(writer, "uniqueidentifier");
    generateTVPDefinitions(writer, "nvarchar(max)");

  }

  private void generateTVPDefinitions(DdlWrite writer, String definition) {
    int pos = definition.indexOf('(');
    String name = pos == -1 ? definition : definition.substring(0, pos);

    dropTVP(writer.dropAllProcs(), name);
    //TVPs are included in "I__create_procs.sql"
    //createTVP(writer.apply(), name, definition);
  }

  private void dropTVP(DdlBuffer ddl, String name) {
    ddl.append("if exists (select name  from sys.types where name = 'ebean_").append(name)
        .append("_tvp') drop type ebean_").append(name).append("_tvp").endOfStatement();
  }

  private void createTVP(DdlBuffer ddl, String name, String definition) {
    ddl.append("if not exists (select name  from sys.types where name = 'ebean_").append(name)
    .append("_tvp') create type ebean_").append(name).append("_tvp as table (c1 ").append(definition).append(")")
        .endOfStatement();
  }

}
