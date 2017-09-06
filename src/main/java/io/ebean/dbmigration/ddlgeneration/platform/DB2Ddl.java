package io.ebean.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.DatabasePlatform;

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
    this.inlineUniqueWhenNullable = false;
  }
  
  public String alterTableAddUniqueConstraint(String tableName, String uqName, String[] columns, boolean notNull) {
    if (notNull) {
      return super.alterTableAddUniqueConstraint(tableName, uqName, columns, true);
    } else {
      // Hmm: Complex workaround: https://www.ibm.com/developerworks/mydeveloperworks/blogs/SQLTips4DB2LUW/entry/unique_where_not_null_indexes26?lang=en
      return "-- NOT SUPPORTED " + super.alterTableAddUniqueConstraint(tableName, uqName, columns, true);
    }
  }

}
