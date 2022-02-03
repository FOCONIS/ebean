package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import java.io.IOException;

import io.ebean.annotation.ConstraintMode;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;

/**
 * DB2 platform specific DDL.
 * 
 * according to the list
 * https://datageek.blog/en/2014/05/06/db2-basics-what-is-a-reorg/ a reorg is
 * necessary after
 * <ol>
 * <li>Data type changes that increase the size of a varchar or vargraphic
 * column
 * <li>Data type changes that decrease the size of a varchar or vargraphic
 * column
 * <li>Altering a column to include NOT NULL
 * <li>Altering a column to inline LOBS
 * <li>Altering a column to compress the system default or turn off compression
 * for the system default
 * <li>Altering a table to enable value compression
 * <li>Altering a table to drop a column
 * <li>Changing the PCTFREE for a table
 * <li>Altering a table to turn APEND mode off
 * <li>Altering a table or index to turn compression on
 * </ol>
 * 
 * This is currently handled by BaseTableDdl
 * 
 */
public class DB2Ddl extends PlatformDdl {
  private static final String MOVE_TABLE = "CALL SYSPROC.ADMIN_MOVE_TABLE(CURRENT_SCHEMA,'%s','%s','%s','%s','','','','','','MOVE')";

  public DB2Ddl(DatabasePlatform platform) {
    super(platform);
    this.dropTableIfExists = "drop table ";
    this.dropSequenceIfExists = "drop sequence ";
    this.dropConstraintIfExists = "NOT USED";
    this.dropIndexIfExists = "NOT USED";
    this.identitySuffix = " generated by default as identity";
    this.columnSetNull = "drop not null";
    this.columnSetType = "set data type ";
    this.inlineUniqueWhenNullable = false;
  }

  @Override
  public String alterTableTablespace(String tablename, String tableSpace, String indexSpace, String lobSpace) {
    return String.format(MOVE_TABLE, tablename, tableSpace, indexSpace, lobSpace);
  }
  
  @Override
  public String alterTableAddUniqueConstraint(String tableName, String uqName, String[] columns,
      String[] nullableColumns) {
    StringBuilder sb = new StringBuilder(300);
    if (nullableColumns == null || nullableColumns.length == 0) {

      sb.append("alter table ").append(lowerTableName(tableName));
      sb.append(" add constraint ").append(maxConstraintName(uqName)).append(" unique ");
      appendColumns(columns, sb);
      return sb.toString();
    }

    if (uqName == null) {
      throw new NullPointerException();
    }
    sb.append("create unique index ").append(maxConstraintName(uqName));
    sb.append(" on ").append(lowerTableName(tableName)).append('(');
    for (int i = 0; i < columns.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      sb.append(lowerColumnName(columns[i]));
    }
    sb.append(") exclude null keys");
    return sb.toString();
  }

  @Override
  public String alterTableDropUniqueConstraint(String tableName, String uniqueConstraintName) {
    return alterTableDropConstraint(tableName, uniqueConstraintName) + "\n"
        + dropIndex(uniqueConstraintName, tableName);
  }

  @Override
  protected void appendForeignKeyOnUpdate(StringBuilder buffer, ConstraintMode mode) {
    // do nothing, no on update clause for db2
  }


  @Override
  public String dropSequence(String sequenceName) {
    StringBuilder sb = new StringBuilder(300);
    sb.append("delimiter $$\n");
    sb.append("begin\n");
    sb.append("if exists (select seqschema from syscat.sequences where seqschema = current_schema and seqname = '")
        .append(maxConstraintName(sequenceName).toUpperCase()).append("') then\n");
    sb.append("  prepare stmt from 'drop sequence ").append(maxConstraintName(sequenceName)).append("';\n");
    sb.append("  execute stmt;\n");
    sb.append("end if;\n");
    sb.append("end$$");
    return sb.toString();
  }
  
  @Override
  public String alterTableDropForeignKey(String tableName, String fkName) {
    return alterTableDropConstraint(tableName, fkName);
  }

  @Override
  public String alterTableDropConstraint(String tableName, String constraintName) {
    StringBuilder sb = new StringBuilder(300);
    sb.append("delimiter $$\n");
    sb.append("begin\n");
    sb.append("if exists (select constname from syscat.tabconst where tabschema = current_schema and constname = '");
    sb.append(maxConstraintName(constraintName).toUpperCase());
    sb.append("' and tabname = '").append(lowerTableName(tableName).toUpperCase()).append("') then\n");
    
    sb.append("  prepare stmt from 'alter table ").append(lowerTableName(tableName));
    sb.append(" drop constraint ").append(maxConstraintName(constraintName)).append("';\n");
    
    sb.append("  execute stmt;\n");
    sb.append("end if;\n");
    sb.append("end$$");
    return sb.toString();
  }
  
  @Override
  public String dropIndex(String indexName, String tableName, boolean concurrent) {
    StringBuilder sb = new StringBuilder(300);
    sb.append("delimiter $$\n");
    sb.append("begin\n");
    sb.append("if exists (select indname from syscat.indexes where indschema = current_schema and indname = '");
    sb.append(maxConstraintName(indexName).toUpperCase()).append("') then\n");

    sb.append("  prepare stmt from 'drop index ").append(maxConstraintName(indexName)).append("';\n");
    sb.append("  execute stmt;\n");
    sb.append("end if;\n");
    sb.append("end$$");
    return sb.toString();
  }

  @Override
  public String reorgTable(String table, int counter) {
    return "call sysproc.admin_cmd('reorg table " + lowerTableName(table) + "') /* reorg #" + counter + " */";
  }
  
  @Override
  public void addTablespace(DdlBuffer apply, String tablespaceName, String indexTablespace, String lobTablespace)
      throws IOException {
    apply.append(" in ").append(tablespaceName).append(" index in ").append(indexTablespace).append(" long in ").append(lobTablespace);
  }
  
}
