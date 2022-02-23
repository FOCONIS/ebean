package io.ebeaninternal.dbmigration.ddlgeneration.platform;


import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.clickhouse.ClickHousePlatform;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebean.config.dbplatform.mysql.MySqlPlatform;
import io.ebean.config.dbplatform.oracle.OraclePlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.Helper;
import io.ebeaninternal.dbmigration.ddlgeneration.PlatformDdlBuilder;
import io.ebeaninternal.dbmigration.migration.AddTableComment;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.dbmigration.migration.Column;
import io.ebeaninternal.dbmigration.migration.CreateTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class BaseTableDdlTest {

  private final DatabaseConfig serverConfig = new DatabaseConfig();
  private final PlatformDdl h2ddl = PlatformDdlBuilder.create(new H2Platform());

  @Test
  public void testAlterColumn() {
    BaseTableDdl ddlGen = new BaseTableDdl(serverConfig, h2ddl);

    BaseDdlWrite write = new BaseDdlWrite();

    AlterColumn alterColumn = new AlterColumn();
    alterColumn.setTableName("mytab");
    alterColumn.setCheckConstraint("check (acol in ('A','B'))");
    alterColumn.setCheckConstraintName("ck_mytab_acol");

    ddlGen.generate(write, alterColumn);

    String ddl = write.toString();
    assertThat(ddl).contains("alter table mytab drop constraint if exists ck_mytab_acol");
    assertThat(ddl).contains("alter table mytab add constraint ck_mytab_acol check (acol in ('A','B'))");
  }

  @Test
  public void testAddColumn_withTypeConversion() {

    BaseTableDdl ddlGen = new BaseTableDdl(serverConfig, PlatformDdlBuilder.create(new OraclePlatform()));

    BaseDdlWrite write = new BaseDdlWrite();

    Column column = new Column();
    column.setName("col_name");
    column.setType("varchar(20)");

    ddlGen.alterTableAddColumn(write, "mytable", column, false, false);

    String ddl = write.toString();
    assertThat(ddl).contains("alter table mytable add col_name varchar2(20)");
  }

  @Test
  public void testAddColumn_withTypeConversion_clickHouseVarchar() {

    ClickHouseTableDdl ddlGen = new ClickHouseTableDdl(serverConfig, PlatformDdlBuilder.create(new ClickHousePlatform()));

    BaseDdlWrite write = new BaseDdlWrite();

    Column column = new Column();
    column.setName("col_name");
    column.setType("varchar(20)");

    ddlGen.alterTableAddColumn(write, "mytable", column, false, false);

    String ddl = write.toString();
    assertThat(ddl).contains("alter table mytable add column col_name String");
  }

  @Test
  public void testAlterColumnComment() {

    BaseTableDdl ddlGen = new BaseTableDdl(serverConfig, h2ddl);

    BaseDdlWrite write = new BaseDdlWrite();

    AlterColumn alterColumn = new AlterColumn();
    alterColumn.setTableName("mytab");
    alterColumn.setColumnName("acol");
    alterColumn.setComment("my comment");

    ddlGen.generate(write, alterColumn);

    String ddl = write.toString();
    assertThat(ddl).contains("comment on column mytab.acol is 'my comment'");
  }

  @Test
  public void alterTableAddColumnWithComment() {
    BaseTableDdl ddl = new BaseTableDdl(serverConfig, h2ddl);
    BaseDdlWrite write = new BaseDdlWrite();
    Column column = new Column();
    column.setName("my_column");
    column.setComment("some comment");
    column.setType("int");

    ddl.alterTableAddColumn(write, "my_table", column, false, false);
    assertEquals(
      "-- altering tables\n"+
      "alter table my_table add column my_column int;\n" +
      "-- post alter\n" +
      "comment on column my_table.my_column is 'some comment';\n", write.toString());
  }

  @Test
  public void testAddTableComment() {

    BaseTableDdl ddlGen = new BaseTableDdl(serverConfig, h2ddl);

    BaseDdlWrite write = new BaseDdlWrite();

    AddTableComment addTableComment = new AddTableComment();
    addTableComment.setName("mytab");
    addTableComment.setComment("my comment");

    ddlGen.generate(write, addTableComment);

    String ddl = write.toString();
    assertThat(ddl).contains("comment on table mytab is 'my comment'");
  }

  @Test
  public void testAddTableComment_mysql() {

    BaseTableDdl ddlGen = new BaseTableDdl(serverConfig, PlatformDdlBuilder.create(new MySqlPlatform()));

    BaseDdlWrite write = new BaseDdlWrite();

    AddTableComment addTableComment = new AddTableComment();
    addTableComment.setName("mytab");
    addTableComment.setComment("my comment");

    ddlGen.generate(write, addTableComment);

    String ddl = write.toString();
    assertThat(ddl).contains("alter table mytab comment = 'my comment'");
  }

  @Test
  public void testGenerate() throws Exception {

    BaseTableDdl ddlGen = new BaseTableDdl(serverConfig, h2ddl);

    BaseDdlWrite write = new BaseDdlWrite();

    ddlGen.generate(write, createTable());

    StringBuilder sb = new StringBuilder();
    write.writeApply(sb);
    assertThat(sb.toString()).isEqualTo(Helper.asText(this, "/assert/BaseTableDdlTest/createTable-apply.txt"));

    sb.setLength(0);
    write.writeDropAll(sb);
    assertThat(sb.toString()).isEqualTo(Helper.asText(this, "/assert/BaseTableDdlTest/createTable-rollback.txt"));
  }

  private CreateTable createTable() {
    CreateTable createTable = new CreateTable();
    createTable.setName("mytable");
    createTable.setPkName("pk_mytable");
    List<Column> columns = createTable.getColumn();
    Column col = new Column();
    col.setName("id");
    col.setType("integer");
    col.setPrimaryKey(true);

    columns.add(col);

    Column col2 = new Column();
    col2.setName("status");
    col2.setType("varchar(1)");
    col2.setNotnull(true);
    col2.setCheckConstraint("check (status in ('A','B'))");
    col2.setCheckConstraintName("ck_mytable_status");

    columns.add(col2);

    Column col3 = new Column();
    col3.setName("order_id");
    col3.setType("integer");
    col3.setNotnull(true);
    col3.setReferences("orders.id");
    col3.setForeignKeyName("fk_mytable_order_id");
    col3.setForeignKeyIndex("ix_mytable_order_id");

    columns.add(col3);

    return createTable;
  }
}
