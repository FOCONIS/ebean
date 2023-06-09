package io.ebean.config.dbplatform;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class BindValidatorMeta {
  private final int jdbcType;
  private final int dbLength;
  private final String table;
  private final String column;
  private final String columnDefn;

  public BindValidatorMeta(int jdbcType, int dbLength, String baseTable, String column, String columnDefn) {
    this.jdbcType = jdbcType;
    this.dbLength = dbLength;
    this.table = baseTable;
    this.column = column;
    this.columnDefn = columnDefn;
  }

  public int getJdbcType() {
    return jdbcType;
  }

  public int getDbLength() {
    return dbLength;
  }

  public String getTable() {
    return table;
  }

  public String getColumn() {
    return column;
  }

  public String getColumnDefn() {
    return columnDefn;
  }
}
