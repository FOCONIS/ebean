package io.ebean.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.dbmigration.ddlgeneration.DdlBuffer;
import io.ebean.dbmigration.ddlgeneration.DdlWrite;
import io.ebean.dbmigration.migration.AlterColumn;
import io.ebean.dbmigration.migration.Column;
import io.ebean.util.StringHelper;

import java.io.IOException;

/**
 * MS SQL Server platform specific DDL.
 */
public class SqlServerDdl extends PlatformDdl {

  public SqlServerDdl(DatabasePlatform platform) {
    super(platform);
    this.identitySuffix = " identity(1,1)";
    this.foreignKeyRestrict = "";
    this.alterTableIfExists = "";
    this.addColumn = "add";
    this.inlineUniqueWhenNullable = false;
    this.columnSetDefault = "add default";
    this.dropConstraintIfExists = "drop constraint";
    this.historyDdl = new SqlServerHistoryDdl();
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
  public String dropIndex(String indexName, String tableName) {
    return "IF EXISTS (SELECT name FROM sys.indexes WHERE object_id = OBJECT_ID('" 
        + tableName +"','U') AND name = '" + maxConstraintName(indexName) + "') drop index " 
        + maxConstraintName(indexName) + " ON " + tableName;
  }
  /**
   * MsSqlServer specific null handling on unique constraints.
   */
  @Override
  public String alterTableAddUniqueConstraint(String tableName, String uqName, String[] columns, String[] nullableColumns) {
    if (nullableColumns == null || nullableColumns.length == 0) {
      return super.alterTableAddUniqueConstraint(tableName, uqName, columns, nullableColumns);
    }
    if (uqName == null) {
      throw new NullPointerException();
    }
    // issues#233
    String start = "create unique nonclustered index " + uqName + " on " + tableName + "(";
    StringBuilder sb = new StringBuilder(start);

    for (int i = 0; i < columns.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(columns[i]);
    }
    sb.append(") where");
    String sep = " ";
    for (String column : nullableColumns) {
      sb.append(sep).append(column).append(" is not null");
      sep = " and ";
    }
    return sb.toString();
  }

  
  public String alterTableDropConstraint(String tableName, String constraintName) {
    StringBuilder sb = new StringBuilder();
    // DF = DeFault, CK = Check Constraint, UQ = Unique Constraint.
    sb.append("IF (OBJECT_ID('").append(maxConstraintName(constraintName)).append("', 'C') IS NOT NULL) ");
    sb.append(super.alterTableDropConstraint(tableName, constraintName));
    return sb.toString();
  }
  /**
   * Drop a unique constraint from the table (Sometimes this is an index).
   */
  @Override
  public String alterTableDropUniqueConstraint(String tableName, String uniqueConstraintName) {
    StringBuilder sb = new StringBuilder();
    sb.append("IF (OBJECT_ID('").append(maxConstraintName(uniqueConstraintName)).append("', 'UQ') IS NOT NULL) ");
    sb.append(super.alterTableDropUniqueConstraint(tableName, uniqueConstraintName)).append(";\n");
    sb.append(dropIndex(uniqueConstraintName, tableName));
    return sb.toString();
  }
  /**
   * Generate and return the create sequence DDL.
   */
  @Override
  public String createSequence(String sequenceName, int initialValue, int allocationSize) {

    StringBuilder sb = new StringBuilder("create sequence ");
    sb.append(sequenceName);
    sb.append(" as bigint ");
    if (initialValue > 1) {
      sb.append(" start with ").append(initialValue);
    } else {
      sb.append(" start with 1 ");
    }
    if (allocationSize > 0 && allocationSize != 50) {
      // at this stage ignoring allocationSize 50 as this is the 'default' and
      // not consistent with the way Ebean batch fetches sequence values
      sb.append(" increment by ").append(allocationSize);
    }
    sb.append(";");
    return sb.toString();
  }
  
  @Override
  public String alterColumnDefaultValue(String tableName, String columnName, String defaultValue) {

    StringBuilder sb = new StringBuilder();
    if (DdlHelp.isDropDefault(defaultValue)) {
      sb.append("delimiter $$\n");
      sb.append("DECLARE @Tmp nvarchar(200);");
      sb.append("select @Tmp = t1.name  from sys.default_constraints t1\n");
      sb.append("  join sys.columns t2 on t1.object_id = t2.default_object_id\n");
      sb.append("  where t1.parent_object_id = OBJECT_ID('").append(tableName)
        .append("') and t2.name = '").append(columnName).append("';\n");
      sb.append("if @Tmp is not null EXEC('alter table ").append(tableName).append(" drop constraint ' + @Tmp)$$");
    } else {
      sb.append("alter table ").append(tableName);
      sb.append(" add default ").append(convertDefaultValue(defaultValue)).append(" for ").append(columnName);
    }
    return sb.toString();
  }

  @Override
  public String alterColumnBaseAttributes(AlterColumn alter) {
    if (DdlHelp.isDropDefault(alter.getDefaultValue())) {
      return null;
    }
    String tableName = alter.getTableName();
    String columnName = alter.getColumnName();
    String type = alter.getType() != null ? alter.getType() : alter.getCurrentType();
    type = convert(type, false);
    boolean notnull = (alter.isNotnull() != null) ? alter.isNotnull() : Boolean.TRUE.equals(alter.isCurrentNotnull());
    String notnullClause = notnull ? " not null" : "";

    return "alter table " + tableName + " " + alterColumn + " " + columnName + " " + type + notnullClause;
  }

  @Override
  public String alterColumnType(String tableName, String columnName, String type) {

    // can't alter itself - done in alterColumnBaseAttributes()
    return null;
  }

  @Override
  public String alterColumnNotnull(String tableName, String columnName, boolean notnull) {

    // can't alter itself - done in alterColumnBaseAttributes()
    return null;
  }

  /**
   * Add table comment as a separate statement (from the create table statement).
   */
  @Override
  public void addTableComment(DdlBuffer apply, String tableName, String tableComment) throws IOException {

    // do nothing for MS SQL Server (cause it requires stored procedures etc)
  }

  /**
   * Add column comment as a separate statement.
   */
  @Override
  public void addColumnComment(DdlBuffer apply, String table, String column, String comment) throws IOException {

    // do nothing for MS SQL Server (cause it requires stored procedures etc)
  }
  /**
   * Write the column definition to the create table statement.
   */
  protected void xwriteColumnDefinition(DdlBuffer buffer, String tableName, Column column, boolean useIdentity) throws IOException {

    boolean identityColumn = useIdentity && isTrue(column.isPrimaryKey());
    String platformType = convert(column.getType(), identityColumn);

    buffer.append("  ");
    buffer.append(lowerColumnName(column.getName()), 29);
    buffer.append(platformType);
    if (!Boolean.TRUE.equals(column.isPrimaryKey())) {
      String defaultValue = convertDefaultValue(column.getDefaultValue());
      if (defaultValue != null) {
        buffer.append(" default ").append(defaultValue);
      }
    }
    if (isTrue(column.isNotnull()) || isTrue(column.isPrimaryKey())) {
      buffer.append(" not null");
    }

    // add check constraints later as we really want to give them a nice name
    // so that the database can potentially provide a nice SQL error
  }


  public void xalterTableAddColumn(DdlBuffer buffer, String tableName, Column column, boolean onHistoryTable, String defaultValue) throws IOException {
    if (onHistoryTable) {
      return;
    }
    
    String convertedType = convert(column.getType(), false);
    buffer.append("alter table ").append(tableName)
      .append(" ").append(addColumn).append(" ").append(column.getName())
      .append(" ").append(convertedType);

    if (isTrue(column.isNotnull())) {
      buffer.append(" not null");
    }
    if (defaultValue != null) {
      buffer.append(" default ").append(defaultValue);
    }
    if (!StringHelper.isNull(column.getCheckConstraint())) {
      buffer.append(", constraint ").append(column.getCheckConstraintName());
      buffer.append(" ").append(column.getCheckConstraint());
    }

    buffer.endOfStatement();
  }
  /**
   * It is rather complex to delete a column on SqlServer as there must not exist any references
   * (constraints, default values, indices and foreign keys). The list is not yet complete, as
   * indices over multiple columns will not yet deleted.
   * (This may be changed to delete all refering objects by using the sys.* tables later)
   */
  @Override
  public void alterTableDropColumn(DdlBuffer buffer, String tableName, String columnName) throws IOException {
    buffer.append("-- drop column ").append(tableName).append(".").append(columnName).endOfStatement();
    
    buffer.append(alterTableDropUniqueConstraint(tableName, naming.uniqueConstraintName(tableName, columnName)));
    buffer.endOfStatement();
    buffer.append(alterColumnDefaultValue(tableName, columnName, DdlHelp.DROP_DEFAULT));
    buffer.endOfStatement();
    buffer.append(alterTableDropConstraint(tableName, naming.checkConstraintName(tableName, columnName)));
    buffer.endOfStatement();
    buffer.append(dropIndex(naming.indexName(tableName, columnName), tableName));
    buffer.endOfStatement();
    buffer.append(alterTableDropForeignKey(tableName, naming.foreignKeyConstraintName(tableName, columnName)));
    buffer.endOfStatement();
    super.alterTableDropColumn(buffer, tableName, columnName);
  }
  
  @Override
  public void generateExtra(DdlWrite write) throws IOException {
    super.generateExtra(write);

    generateTVPDefinitions(write, "bigint");
    generateTVPDefinitions(write, "float");
    generateTVPDefinitions(write, "bit");
    generateTVPDefinitions(write, "date");
    generateTVPDefinitions(write, "time");
    generateTVPDefinitions(write, "datetime2");
    generateTVPDefinitions(write, "nvarchar(max)");

  }

  private void generateTVPDefinitions(DdlWrite write, String definition) throws IOException {
    int pos = definition.indexOf('(');
    String name = pos == -1 ? definition : definition.substring(0, pos);
    
    dropTVP(write.dropAll(), name);
    dropTVP(write.apply(), name);
    createTVP(write.apply(), name, definition);
  }
  
  private void dropTVP(DdlBuffer ddl, String name) throws IOException {
    ddl.append("if exists (select name  from sys.types where name = 'ebean_")
    .append(name)
    .append("_tvp') drop type ebean_").append(name).append("_tvp").endOfStatement();
  }
  private void createTVP(DdlBuffer ddl, String name, String definition) throws IOException {
    ddl.append("create type ebean_").append(name).append("_tvp as table (c1 ")
    .append(definition).append(")").endOfStatement();
  }

}
