package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.annotation.ConstraintMode;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;

/**
 * DB2 platform specific DDL.
 */
public class DB2Ddl extends PlatformDdl {

  public DB2Ddl(DatabasePlatform platform) {
    super(platform);
    this.dropTableIfExists = "drop table ";
    this.dropSequenceIfExists = "drop sequence ";
    this.dropConstraintIfExists = "drop constraint";
    this.dropIndexIfExists = "drop index ";
    this.identitySuffix = " generated by default as identity";
    this.columnSetNull = "drop not null";
    this.columnSetType = "set data type";
    this.inlineUniqueWhenNullable = false;
  }

  @Override
  public void alterTableAddUniqueConstraint(DdlWrite writer, String tableName, String uqName, String[] columns, String[] nullableColumns) {
    if (nullableColumns == null || nullableColumns.length == 0) {
       super.alterTableAddUniqueConstraint(writer, tableName, uqName, columns, nullableColumns);
       return;
    }     

    if (uqName == null) {
      throw new NullPointerException();
    }
    
    DdlBuffer buffer = writer.index().append("create unique index ")
        .append(uqName).append(" on ").append(tableName).append("(");

    for (int i = 0; i < columns.length; i++) {
      if (i > 0) {
        buffer.append(",");
      }
      buffer.append(columns[i]);
    }
    buffer.append(") exclude null keys");
  }

  @Override
  protected void appendForeignKeyOnUpdate(StringBuilder buffer, ConstraintMode mode) {
    // do nothing, no on update clause for db2
  }

}
