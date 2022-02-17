package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.DB;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.IdType;
import io.ebean.config.dbplatform.db2.DB2LuwPlatform;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebean.config.dbplatform.hana.HanaPlatform;
import io.ebean.config.dbplatform.mysql.MySqlPlatform;
import io.ebean.config.dbplatform.oracle.OraclePlatform;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;
import io.ebean.config.dbplatform.sqlserver.SqlServer17Platform;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.PlatformDdlBuilder;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.dbmigration.migration.AlterForeignKey;
import io.ebeaninternal.dbmigration.migration.Column;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlatformDdl_AlterColumnTest {

  private static boolean useV1Syntax = Boolean.getBoolean("ebean.h2.useV1Syntax");

  private final PlatformDdl h2Ddl = PlatformDdlBuilder.create(new H2Platform());
  private final PlatformDdl pgDdl = PlatformDdlBuilder.create(new PostgresPlatform());
  private final PlatformDdl mysqlDdl = PlatformDdlBuilder.create(new MySqlPlatform());
  private final PlatformDdl oraDdl = PlatformDdlBuilder.create(new OraclePlatform());
  private final PlatformDdl sqlServerDdl = PlatformDdlBuilder.create(new SqlServer17Platform());
  private final PlatformDdl hanaDdl = PlatformDdlBuilder.create(new HanaPlatform());
  private final PlatformDdl db2LuwDdl = PlatformDdlBuilder.create(new DB2LuwPlatform());

  {
    DatabaseConfig serverConfig = DB.getDefault().pluginApi().config();
    sqlServerDdl.configure(serverConfig);
  }

  AlterColumn alterNotNull() {
    AlterColumn alterColumn = new AlterColumn();
    alterColumn.setTableName("mytab");
    alterColumn.setColumnName("acol");
    alterColumn.setCurrentType("varchar(5)");
    alterColumn.setNotnull(Boolean.TRUE);

    return alterColumn;
  }

  @Test
  public void convertArrayType_default() {
    assertThat(mysqlDdl.convertArrayType("varchar[](90)")).isEqualTo("varchar(90)");
    assertThat(mysqlDdl.convertArrayType("integer[](60)")).isEqualTo("varchar(60)");
    assertThat(mysqlDdl.convertArrayType("varchar[]")).isEqualTo("varchar(1000)");
    assertThat(mysqlDdl.convertArrayType("integer[]")).isEqualTo("varchar(1000)");
  }

  @Test
  public void convertArrayType_h2() {
    if (useV1Syntax) {
      assertThat(h2Ddl.convertArrayType("varchar[](90)")).isEqualTo("array");
      assertThat(h2Ddl.convertArrayType("integer[](60)")).isEqualTo("array");
      assertThat(h2Ddl.convertArrayType("varchar[]")).isEqualTo("array");
      assertThat(h2Ddl.convertArrayType("integer[]")).isEqualTo("array");
    } else {
      assertThat(h2Ddl.convertArrayType("varchar[](90)")).isEqualTo("varchar array");
      assertThat(h2Ddl.convertArrayType("integer[](60)")).isEqualTo("integer array");
      assertThat(h2Ddl.convertArrayType("varchar[]")).isEqualTo("varchar array");
      assertThat(h2Ddl.convertArrayType("integer[]")).isEqualTo("integer array");
    }
  }

  @Test
  public void convertArrayType_postgres() {
    assertThat(pgDdl.convertArrayType("varchar[](90)")).isEqualTo("varchar[]");
    assertThat(pgDdl.convertArrayType("integer[](60)")).isEqualTo("integer[]");
    assertThat(pgDdl.convertArrayType("varchar[]")).isEqualTo("varchar[]");
    assertThat(pgDdl.convertArrayType("integer[]")).isEqualTo("integer[]");
  }

  @Test
  public void convertArrayType_hana() {
    assertThat(hanaDdl.convertArrayType("varchar[](90)")).isEqualTo("nvarchar(255) array(90)");
    assertThat(hanaDdl.convertArrayType("integer[](60)")).isEqualTo("integer array(60)");
    assertThat(hanaDdl.convertArrayType("varchar[]")).isEqualTo("nvarchar(255) array");
    assertThat(hanaDdl.convertArrayType("integer[]")).isEqualTo("integer array");
  }
  
  private String alterColumnBaseAttributes(PlatformDdl ddl, AlterColumn alterColumn) throws IOException {
    DdlWrite write = new BaseDdlWrite();
    ddl.alterColumnBaseAttributes(write, alterColumn);
    return write.toString();
  }

  @Test
  public void testAlterColumnBaseAttributes() throws IOException {

    AlterColumn alterColumn = alterNotNull();
    assertEquals("", alterColumnBaseAttributes(h2Ddl, alterColumn));
    assertEquals("", alterColumnBaseAttributes(pgDdl, alterColumn));
    assertEquals("", alterColumnBaseAttributes(oraDdl, alterColumn));

    String sql = alterColumnBaseAttributes(mysqlDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab modify acol varchar(5) not null;\n", sql);

    sql = alterColumnBaseAttributes(sqlServerDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab alter column acol nvarchar(5) not null;\n", sql);

    sql = alterColumnBaseAttributes(hanaDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab alter ( acol nvarchar(5) not null);\n", sql);

    alterColumn.setNotnull(Boolean.FALSE);
    sql = alterColumnBaseAttributes(mysqlDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab modify acol varchar(5);\n", sql);

    sql = alterColumnBaseAttributes(hanaDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab alter ( acol nvarchar(5));\n", sql);

    alterColumn.setNotnull(null);
    alterColumn.setType("varchar(100)");

    sql = alterColumnBaseAttributes(mysqlDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab modify acol varchar(100);\n", sql);

    sql = alterColumnBaseAttributes(hanaDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab alter ( acol nvarchar(100));\n", sql);

    alterColumn.setCurrentNotnull(Boolean.TRUE);
    sql = alterColumnBaseAttributes(mysqlDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab modify acol varchar(100) not null;\n", sql);

    sql = alterColumnBaseAttributes(hanaDdl, alterColumn);
    assertEquals("-- altering tables\nalter table mytab alter ( acol nvarchar(100) not null);\n", sql);
  }

  private String alterColumnType(PlatformDdl ddl, String type) throws IOException {
    DdlWrite write = new BaseDdlWrite();
    ddl.alterColumnType(write, "mytab", "acol", type);
    return write.toString();
  }

  @Test
  public void testAlterColumnType() throws IOException {

    String sql = alterColumnType(h2Ddl, "varchar(20)");
    assertEquals("-- altering tables\nalter table mytab alter column acol varchar(20);\n", sql);

    sql = alterColumnType(pgDdl, "varchar(20)");
    assertEquals("-- altering tables\nalter table mytab alter column acol type varchar(20) using acol::varchar(20);\n", sql);
    sql = alterColumnType(pgDdl, "bigint");
    assertEquals("-- altering tables\nalter table mytab alter column acol type bigint using acol::bigint;\n", sql);

    sql = alterColumnType(oraDdl, "varchar(20)");
    assertEquals("-- altering tables\nalter table mytab modify acol varchar2(20);\n", sql);

    sql = alterColumnType(mysqlDdl, "varchar(20)");
    assertEquals("", sql);

    sql = alterColumnType(sqlServerDdl, "varchar(20)");
    assertEquals("", sql);

    sql = alterColumnType(hanaDdl, "varchar(20)");
    assertEquals("", sql);
  }

  private String alterColumnNotNull(PlatformDdl ddl, boolean notnull) throws IOException {
    DdlWrite write = new BaseDdlWrite();
    ddl.alterColumnNotnull(write, "mytab", "acol", notnull);
    return write.toString();
  }

  @Test
  public void testAlterColumnNotnull() throws IOException {

    String sql = alterColumnNotNull(h2Ddl, true);
    assertEquals("-- altering tables\nalter table mytab alter column acol set not null;\n", sql);

    sql = alterColumnNotNull(pgDdl, true);
    assertEquals("-- altering tables\nalter table mytab alter column acol set not null;\n", sql);

    sql = alterColumnNotNull(oraDdl, true);
    assertEquals("-- altering tables\nalter table mytab modify acol not null;\n", sql);

    sql = alterColumnNotNull(mysqlDdl, true);
    assertEquals("", sql);

    sql = alterColumnNotNull(sqlServerDdl, true);
    assertEquals("", sql);

    sql = alterColumnNotNull(hanaDdl, true);
    assertEquals("", sql);
    
    sql = alterColumnNotNull(db2LuwDdl, true);
    assertEquals("-- altering tables\nalter table mytab alter column acol set not null;\n", sql);
  }

  @Test
  public void testAlterColumnNull() throws IOException {

    String sql = alterColumnNotNull(h2Ddl, false);
    assertEquals("-- altering tables\nalter table mytab alter column acol set null;\n", sql);

    sql = alterColumnNotNull(pgDdl, false);
    assertEquals("-- altering tables\nalter table mytab alter column acol drop not null;\n", sql);

    sql = alterColumnNotNull(oraDdl, false);
    assertEquals("-- altering tables\nalter table mytab modify acol null;\n", sql);
    
    sql = alterColumnNotNull(db2LuwDdl, false);
    assertEquals("-- altering tables\nalter table mytab alter column acol drop not null;\n", sql);

    sql = alterColumnNotNull(mysqlDdl, false);
    assertEquals("",sql);

    sql = alterColumnNotNull(sqlServerDdl, false);
    assertEquals("",sql);

    sql = alterColumnNotNull(hanaDdl, false);
    assertEquals("",sql);
  }

  private String alterColumnDefaultValue(PlatformDdl ddl, String value) {
    DdlWrite write = new BaseDdlWrite();
    ddl.alterColumnDefaultValue(write, "mytab", "acol", value);
    return write.toString();
  }
  @Test
  public void testAlterColumnDefaultValue() {

    String sql = alterColumnDefaultValue(h2Ddl, "'hi'");
    assertEquals("-- altering tables\nalter table mytab alter column acol set default 'hi';\n", sql);

    sql = alterColumnDefaultValue(pgDdl, "'hi'");
    assertEquals("-- altering tables\nalter table mytab alter column acol set default 'hi';\n", sql);

    sql = alterColumnDefaultValue(oraDdl, "'hi'");
    assertEquals("-- altering tables\nalter table mytab modify acol default 'hi';\n", sql);

    sql = alterColumnDefaultValue(mysqlDdl, "'hi'");
    assertEquals("-- altering tables\nalter table mytab alter acol set default 'hi';\n", sql);

    sql = alterColumnDefaultValue(sqlServerDdl, "'hi'");
    assertEquals("-- altering tables\nalter table mytab add default 'hi' for acol;\n", sql);

    boolean exceptionCaught = false;
    try {
      alterColumnDefaultValue(hanaDdl, "'hi'");
    } catch (UnsupportedOperationException e) {
      exceptionCaught = true;
    }
    assertTrue(exceptionCaught);
  }

  @Test
  public void testAlterColumnDropDefault() {

    String sql = alterColumnDefaultValue(h2Ddl, "DROP DEFAULT");
    assertEquals("-- altering tables\nalter table mytab alter column acol drop default;\n", sql);

    sql = alterColumnDefaultValue(pgDdl, "DROP DEFAULT");
    assertEquals("-- altering tables\nalter table mytab alter column acol drop default;\n", sql);

    sql = alterColumnDefaultValue(oraDdl, "DROP DEFAULT");
    assertEquals("-- altering tables\nalter table mytab modify acol drop default;\n", sql);

    sql = alterColumnDefaultValue(mysqlDdl, "DROP DEFAULT");
    assertEquals("-- altering tables\nalter table mytab alter acol drop default;\n", sql);

    sql = alterColumnDefaultValue(sqlServerDdl, "DROP DEFAULT");
    assertEquals("-- apply changes\nEXEC usp_ebean_drop_default_constraint mytab, acol;\n", sql);
    
    sql = alterColumnDefaultValue(db2LuwDdl, "DROP DEFAULT");
    assertEquals("-- altering tables\nalter table mytab alter column acol drop default;\n", sql);
    
    boolean exceptionCaught = false;
    try {
      alterColumnDefaultValue(hanaDdl, "DROP DEFAULT");
    } catch (UnsupportedOperationException e) {
      exceptionCaught = true;
    }
    assertTrue(exceptionCaught);
  }

  @Test
  public void oracle_alterTableAddColumn() throws IOException {
    DdlWrite write = new BaseDdlWrite();
    oraDdl.alterTableAddColumn(write, "my_table", simpleColumn(), false, "1");
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table my_table add my_column int default 1 not null;\n");
  }

  private Column simpleColumn() {
    Column column = new Column();
    column.setName("my_column");
    column.setType("int");
    column.setNotnull(true);
    column.setDefaultValue("1");
    return column;
  }

  @Test
  public void useIdentityType_h2() {
    assertEquals(h2Ddl.useIdentityType(null), IdType.IDENTITY);
    assertEquals(h2Ddl.useIdentityType(IdType.SEQUENCE), IdType.SEQUENCE);
    assertEquals(h2Ddl.useIdentityType(IdType.IDENTITY), IdType.IDENTITY);
    assertEquals(h2Ddl.useIdentityType(IdType.GENERATOR), IdType.GENERATOR);
    assertEquals(h2Ddl.useIdentityType(IdType.EXTERNAL), IdType.EXTERNAL);
  }

  @Test
  public void useIdentityType_postgres() {
    assertEquals(pgDdl.useIdentityType(IdType.GENERATOR), IdType.GENERATOR);
    assertEquals(pgDdl.useIdentityType(IdType.EXTERNAL), IdType.EXTERNAL);

    assertEquals(pgDdl.useIdentityType(null), IdType.IDENTITY);
    assertEquals(pgDdl.useIdentityType(IdType.SEQUENCE), IdType.SEQUENCE);
    assertEquals(pgDdl.useIdentityType(IdType.IDENTITY), IdType.IDENTITY);
  }

  @Test
  public void useIdentityType_mysql() {

    assertEquals(mysqlDdl.useIdentityType(null), IdType.IDENTITY);
    assertEquals(mysqlDdl.useIdentityType(IdType.SEQUENCE), IdType.IDENTITY);
    assertEquals(mysqlDdl.useIdentityType(IdType.IDENTITY), IdType.IDENTITY);
    assertEquals(mysqlDdl.useIdentityType(IdType.GENERATOR), IdType.GENERATOR);
    assertEquals(mysqlDdl.useIdentityType(IdType.EXTERNAL), IdType.EXTERNAL);
  }

  @Test
  public void useIdentityType_oracle() {

    assertEquals(oraDdl.useIdentityType(null), IdType.IDENTITY);
    assertEquals(oraDdl.useIdentityType(IdType.SEQUENCE), IdType.SEQUENCE);
    assertEquals(oraDdl.useIdentityType(IdType.IDENTITY), IdType.IDENTITY);
    assertEquals(oraDdl.useIdentityType(IdType.GENERATOR), IdType.GENERATOR);
    assertEquals(oraDdl.useIdentityType(IdType.EXTERNAL), IdType.EXTERNAL);
  }

  @Test
  public void useIdentityType_hana() {

    assertEquals(hanaDdl.useIdentityType(null), IdType.IDENTITY);
    assertEquals(hanaDdl.useIdentityType(IdType.SEQUENCE), IdType.IDENTITY);
    assertEquals(hanaDdl.useIdentityType(IdType.IDENTITY), IdType.IDENTITY);
    assertEquals(hanaDdl.useIdentityType(IdType.GENERATOR), IdType.GENERATOR);
    assertEquals(hanaDdl.useIdentityType(IdType.EXTERNAL), IdType.EXTERNAL);
  }

  @Test
  public void appendForeignKeySuffix_when_defaults() {
    assertThat(alterFkey(null, null)).isEqualTo(" on delete restrict on update restrict");
  }

  @Test
  public void appendForeignKeySuffix_when_RestrictSetNull() {
    assertThat(alterFkey("RESTRICT", "SET_NULL")).isEqualTo(" on delete restrict on update set null");
  }

  @Test
  public void appendForeignKeySuffix_when_SetNullRestrict() {
    assertThat(alterFkey("SET_NULL", "RESTRICT")).isEqualTo(" on delete set null on update restrict");
  }

  @Test
  public void appendForeignKeySuffix_when_SetDefaultCascade() {
    assertThat(alterFkey("SET_DEFAULT", "CASCADE")).isEqualTo(" on delete set default on update cascade");
  }

  private String alterFkey(String onDelete, String onUpdate) {
    AlterForeignKey afk = new AlterForeignKey();
    afk.setOnDelete(onDelete);
    afk.setOnUpdate(onUpdate);
    StringBuilder buffer = new StringBuilder();
    h2Ddl.appendForeignKeySuffix(new WriteForeignKey(afk), buffer);
    return buffer.toString();
  }

}
