package io.ebeaninternal.dbmigration.ddlgeneration;

import io.localtest.BaseTestCase;
import io.ebean.DB;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebean.config.dbplatform.hana.HanaPlatform;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;
import io.ebean.config.dbplatform.sqlserver.SqlServer17Platform;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.dbmigration.migration.ChangeSet;
import io.ebeaninternal.dbmigration.model.CurrentModel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseDdlHandlerTest extends BaseTestCase {

  private static boolean useV1Syntax = Boolean.getBoolean("ebean.h2.useV1Syntax");
  
  private final DatabaseConfig serverConfig = new DatabaseConfig();

  private DdlHandler handler(DatabasePlatform platform) {
    return PlatformDdlBuilder.create(platform).createDdlHandler(serverConfig);
  }

  private DdlHandler h2Handler() {
    return handler(new H2Platform());
  }

  private DdlHandler postgresHandler() {
    return handler(new PostgresPlatform());
  }

  private DdlHandler sqlserverHandler() {
    return handler(new SqlServer17Platform());
  }

  private DdlHandler hanaHandler() {
    return handler(new HanaPlatform());
  }

  @Test
  public void addColumn_nullable_noConstraint() throws Exception {

    DdlWrite writer = new DdlWrite();
    h2Handler().generate(writer, Helper.getAddColumn());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column added_to_foo varchar(20);\n\n");

    writer = new DdlWrite();
    sqlserverHandler().generate(writer, Helper.getAddColumn());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add added_to_foo nvarchar(20);\n\n");

    writer = new DdlWrite();
    hanaHandler().generate(writer, Helper.getAddColumn());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add ( added_to_foo nvarchar(20));\n\n");
  }

  @Test
  public void addColumn_withCheckConstraint() throws Exception {

    DdlWrite writer = new DdlWrite();
    h2Handler().generate(writer, Helper.getAlterTableAddColumnWithCheckConstraint());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column status integer;\n"
        + "alter table foo add constraint ck_ordering_status check ( status in (0,1));\n\n");

    writer = new DdlWrite();
    hanaHandler().generate(writer, Helper.getAlterTableAddColumnWithCheckConstraint());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add ( status integer);\n"
        + "alter table foo add constraint ck_ordering_status check ( status in (0,1));\n\n");
  }

  /**
   * Test the functionality of the Ebean {@literal @}DbArray extension during DDL
   * generation.
   */
  @Test
  public void addColumn_dbarray() throws Exception {

    DdlWrite writer = new DdlWrite();

    DdlHandler postgresHandler = postgresHandler();
    postgresHandler.generate(writer, Helper.getAlterTableAddDbArrayColumn());

    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column dbarray_added_to_foo varchar[];\n\n");

    writer = new DdlWrite();

    DdlHandler sqlserverHandler = sqlserverHandler();
    sqlserverHandler.generate(writer, Helper.getAlterTableAddDbArrayColumn());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add dbarray_added_to_foo varchar(1000);\n\n");

    writer = new DdlWrite();

    DdlHandler hanaHandler = hanaHandler();
    hanaHandler.generate(writer, Helper.getAlterTableAddDbArrayColumn());
    assertThat(writer.apply().getBuffer())
      .isEqualTo("alter table foo add ( dbarray_added_to_foo nvarchar(255) array);\n\n");
  }

  @Test
  public void addColumn_dbarray_withLength() throws Exception {

    DdlWrite writer = new DdlWrite();

    postgresHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnWithLength());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column dbarray_ninety varchar[];\n\n");

    writer = new DdlWrite();
    h2Handler().generate(writer, Helper.getAlterTableAddDbArrayColumnWithLength());
    if (useV1Syntax) {
      assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column dbarray_ninety array;\n\n");
    } else {
      assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column dbarray_ninety varchar array;\n\n");
    }

    writer = new DdlWrite();
    sqlserverHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnWithLength());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add dbarray_ninety varchar(90);\n\n");

    writer = new DdlWrite();
    hanaHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnWithLength());
    assertThat(writer.apply().getBuffer())
      .isEqualTo("alter table foo add ( dbarray_ninety nvarchar(255) array(90));\n\n");
  }

  @Test
  public void addColumn_dbarray_integer_withLength() throws Exception {

    DdlWrite writer = new DdlWrite();
    postgresHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column dbarray_integer integer[];\n\n");

    writer = new DdlWrite();
    h2Handler().generate(writer, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    if (useV1Syntax) {
      assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column dbarray_integer array;\n\n");
    } else {
      assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add column dbarray_integer integer array;\n\n");
    }

    writer = new DdlWrite();
    sqlserverHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add dbarray_integer varchar(90);\n\n");

    writer = new DdlWrite();
    sqlserverHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnInteger());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add dbarray_integer varchar(1000);\n\n");

    writer = new DdlWrite();
    hanaHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add ( dbarray_integer integer array(90));\n\n");

    writer = new DdlWrite();
    hanaHandler().generate(writer, Helper.getAlterTableAddDbArrayColumnInteger());
    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo add ( dbarray_integer integer array);\n\n");
  }

  @Test
  public void addColumn_withForeignKey() throws Exception {

    DdlWrite writer = new DdlWrite();

    DdlHandler handler = h2Handler();
    handler.generate(writer, Helper.getAlterTableAddColumn());

    String buffer = writer.apply().getBuffer();
    assertThat(buffer).contains("alter table foo add column some_id integer;");

    String fkBuffer = writer.applyForeignKeys().getBuffer();
    assertThat(fkBuffer).contains(
        "alter table foo add constraint fk_foo_some_id foreign key (some_id) references bar (id) on delete restrict on update restrict;");
    assertThat(fkBuffer).contains("create index idx_foo_some_id on foo (some_id);");
    assertThat(writer.dropAll().getBuffer()).isEqualTo("");
  }

  @Test
  public void dropColumn() throws Exception {

    DdlWrite writer = new DdlWrite();
    DdlHandler handler = h2Handler();

    handler.generate(writer, Helper.getDropColumn());

    assertThat(writer.apply().getBuffer()).isEqualTo("alter table foo drop column col2;\n");
    assertThat(writer.dropAll().getBuffer()).isEqualTo("");

    writer = new DdlWrite();
    DdlHandler hanaHandler = hanaHandler();

    hanaHandler.generate(writer, Helper.getDropColumn());

    assertThat(writer.apply().getBuffer()).isEqualTo("CALL usp_ebean_drop_column('foo', 'col2');\n");
    assertThat(writer.dropAll().getBuffer()).isEqualTo("");
  }

  @Test
  public void createTable() throws Exception {

    DdlWrite writer = new DdlWrite();
    DdlHandler handler = h2Handler();

    handler.generate(writer, Helper.getCreateTable());

    String createTableDDL = Helper.asText(this, "/assert/create-table.txt");

    assertThat(writer.apply().getBuffer()).isEqualTo(createTableDDL);
    assertThat(writer.dropAll().getBuffer().trim()).isEqualTo("drop table if exists foo;");

    writer = new DdlWrite();
    DdlHandler hanaHandler = hanaHandler();

    hanaHandler.generate(writer, Helper.getCreateTable());

    String createColumnTableDDL = Helper.asText(this, "/assert/create-column-table.txt");

    assertThat(writer.apply().getBuffer()).isEqualTo(createColumnTableDDL);
    assertThat(writer.dropAll().getBuffer().trim()).isEqualTo("drop table foo cascade;");
  }

  @Test
  public void generateChangeSet() throws Exception {

    DdlWrite writer = new DdlWrite();
    DdlHandler handler = h2Handler();

    handler.generate(writer, Helper.getChangeSet());

    String apply = Helper.asText(this, "/assert/BaseDdlHandlerTest/baseApply.sql");
    String rollbackLast = Helper.asText(this, "/assert/BaseDdlHandlerTest/baseDropAll.sql");

    assertThat(writer.apply().getBuffer()).isEqualTo(apply);
    assertThat(writer.dropAll().getBuffer()).isEqualTo(rollbackLast);
  }

  @Disabled
  @Test
  public void generateChangeSetFromModel() throws Exception {

    SpiEbeanServer defaultServer = (SpiEbeanServer) DB.getDefault();

    ChangeSet createChangeSet = new CurrentModel(defaultServer).getChangeSet();

    DdlWrite writer = new DdlWrite();

    DdlHandler handler = h2Handler();
    handler.generate(writer, createChangeSet);

    String apply = Helper.asText(this, "/assert/changeset-apply.txt");
    String rollbackLast = Helper.asText(this, "/assert/changeset-dropAll.txt");

    assertThat(writer.apply().getBuffer()).isEqualTo(apply);
    assertThat(writer.dropAll().getBuffer()).isEqualTo(rollbackLast);
  }

  @Disabled
  @Test
  public void generateChangeSetFromModel_given_postgresTypes() throws Exception {
    SpiEbeanServer defaultServer = (SpiEbeanServer) DB.getDefault();

    ChangeSet createChangeSet = new CurrentModel(defaultServer).getChangeSet();

    DdlWrite writer = new DdlWrite();

    DdlHandler handler = postgresHandler();
    handler.generate(writer, createChangeSet);

    String apply = Helper.asText(this, "/assert/changeset-pg-apply.sql");
    String applyLast = Helper.asText(this, "/assert/changeset-pg-applyLast.sql");
    String rollbackFirst = Helper.asText(this, "/assert/changeset-pg-rollbackFirst.sql");
    String rollbackLast = Helper.asText(this, "/assert/changeset-pg-rollbackLast.sql");

    assertThat(writer.apply().getBuffer()).isEqualTo(apply);
    assertThat(writer.applyForeignKeys().getBuffer()).isEqualTo(applyLast);
    assertThat(writer.dropAllForeignKeys().getBuffer()).isEqualTo(rollbackFirst);
    assertThat(writer.dropAll().getBuffer()).isEqualTo(rollbackLast);
  }

}
