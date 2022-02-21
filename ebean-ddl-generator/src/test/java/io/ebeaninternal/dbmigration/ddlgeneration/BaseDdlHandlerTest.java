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

    BaseDdlWrite write = new BaseDdlWrite();
    h2Handler().generate(write, Helper.getAddColumn());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column added_to_foo varchar(20);\n");

    write = new BaseDdlWrite();
    sqlserverHandler().generate(write, Helper.getAddColumn());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add added_to_foo nvarchar(20);\n");

    write = new BaseDdlWrite();
    hanaHandler().generate(write, Helper.getAddColumn());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add ( added_to_foo nvarchar(20));\n");
  }

  @Test
  public void addColumn_withCheckConstraint() throws Exception {

    BaseDdlWrite write = new BaseDdlWrite();
    h2Handler().generate(write, Helper.getAlterTableAddColumnWithCheckConstraint());
    assertThat(write.toString()).isEqualTo("-- altering tables\n"
        + "alter table foo add column status integer;\n"
        + "alter table foo add constraint ck_ordering_status check ( status in (0,1));\n");

    write = new BaseDdlWrite();
    hanaHandler().generate(write, Helper.getAlterTableAddColumnWithCheckConstraint());
    assertThat(write.toString()).isEqualTo("-- altering tables\n"
        + "alter table foo add ( status integer);\n"
        + "alter table foo add constraint ck_ordering_status check ( status in (0,1));\n");
  }

  /**
   * Test the functionality of the Ebean {@literal @}DbArray extension during DDL
   * generation.
   */
  @Test
  public void addColumn_dbarray() throws Exception {

    BaseDdlWrite write = new BaseDdlWrite();

    DdlHandler postgresHandler = postgresHandler();
    postgresHandler.generate(write, Helper.getAlterTableAddDbArrayColumn());

    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column dbarray_added_to_foo varchar[];\n");

    write = new BaseDdlWrite();

    DdlHandler sqlserverHandler = sqlserverHandler();
    sqlserverHandler.generate(write, Helper.getAlterTableAddDbArrayColumn());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add dbarray_added_to_foo varchar(1000);\n");

    write = new BaseDdlWrite();

    DdlHandler hanaHandler = hanaHandler();
    hanaHandler.generate(write, Helper.getAlterTableAddDbArrayColumn());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add ( dbarray_added_to_foo nvarchar(255) array);\n");
  }

  @Test
  public void addColumn_dbarray_withLength() throws Exception {

    BaseDdlWrite write = new BaseDdlWrite();

    postgresHandler().generate(write, Helper.getAlterTableAddDbArrayColumnWithLength());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column dbarray_ninety varchar[];\n");

    write = new BaseDdlWrite();
    h2Handler().generate(write, Helper.getAlterTableAddDbArrayColumnWithLength());
    if (useV1Syntax) {
      assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column dbarray_ninety array;\n");
    } else {
      assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column dbarray_ninety varchar array;\n");
    }

    write = new BaseDdlWrite();
    sqlserverHandler().generate(write, Helper.getAlterTableAddDbArrayColumnWithLength());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add dbarray_ninety varchar(90);\n");

    write = new BaseDdlWrite();
    hanaHandler().generate(write, Helper.getAlterTableAddDbArrayColumnWithLength());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add ( dbarray_ninety nvarchar(255) array(90));\n");
  }

  @Test
  public void addColumn_dbarray_integer_withLength() throws Exception {

    BaseDdlWrite write = new BaseDdlWrite();
    postgresHandler().generate(write, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column dbarray_integer integer[];\n");

    write = new BaseDdlWrite();
    h2Handler().generate(write, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    if (useV1Syntax) {
      assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column dbarray_integer array;\n");
    } else {
      assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add column dbarray_integer integer array;\n");
    }

    write = new BaseDdlWrite();
    sqlserverHandler().generate(write, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add dbarray_integer varchar(90);\n");

    write = new BaseDdlWrite();
    sqlserverHandler().generate(write, Helper.getAlterTableAddDbArrayColumnInteger());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add dbarray_integer varchar(1000);\n");

    write = new BaseDdlWrite();
    hanaHandler().generate(write, Helper.getAlterTableAddDbArrayColumnIntegerWithLength());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add ( dbarray_integer integer array(90));\n");

    write = new BaseDdlWrite();
    hanaHandler().generate(write, Helper.getAlterTableAddDbArrayColumnInteger());
    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo add ( dbarray_integer integer array);\n");
  }

  @Test
  public void addColumn_withForeignKey() throws Exception {

    BaseDdlWrite write = new BaseDdlWrite();

    DdlHandler handler = h2Handler();
    handler.generate(write, Helper.getAlterTableAddColumn());

    String buffer = write.toString();
    assertThat(buffer)
      .isEqualTo("-- drop all indices\n"
          + "alter table foo drop constraint if exists fk_foo_some_id;\n"
          + "drop index if exists idx_foo_some_id;\n"
          + "\n"
          + "-- altering tables\n"
          + "alter table foo add column some_id integer;\n"
          + "-- indices/constraints\n"
          + "create index idx_foo_some_id on foo (some_id);\n"
          + "alter table foo add constraint fk_foo_some_id foreign key (some_id) references bar (id) on delete restrict on update restrict;\n"
          + "\n");
  }

  @Test
  public void dropColumn() throws Exception {

    BaseDdlWrite write = new BaseDdlWrite();
    DdlHandler handler = h2Handler();

    handler.generate(write, Helper.getDropColumn());

    assertThat(write.toString()).isEqualTo("-- altering tables\nalter table foo drop column col2;\n");

    write = new BaseDdlWrite();
    DdlHandler hanaHandler = hanaHandler();

    hanaHandler.generate(write, Helper.getDropColumn());

    assertThat(write.toString()).isEqualTo("-- apply changes\nCALL usp_ebean_drop_column('foo', 'col2');\n");
  }

  @Test
  public void createTable() throws Exception {

    DdlWrite write = new BaseDdlWrite();
    DdlHandler handler = h2Handler();

    handler.generate(write, Helper.getCreateTable());

    String ddl = Helper.asText(this, "/assert/drop-and-create-table.txt");

    assertThat(write.toString()).isEqualTo(ddl);
  }

  @Test
  public void createColumnTable() throws Exception {

    DdlWrite write = new BaseDdlWrite();
    DdlHandler hanaHandler = hanaHandler();

    hanaHandler.generate(write, Helper.getCreateTable());

    String ddl = Helper.asText(this, "/assert/drop-and-create-column-table.txt");

    assertThat(write.toString()).isEqualTo(ddl);
  }

  @Test
  public void generateChangeSet() throws Exception {

    BaseDdlWrite write = new BaseDdlWrite();
    DdlHandler handler = h2Handler();

    handler.generate(write, Helper.getChangeSet());

    String apply = Helper.asText(this, "/assert/BaseDdlHandlerTest/baseApply.sql");
    String dropAll = Helper.asText(this, "/assert/BaseDdlHandlerTest/baseDropAll.sql");

    StringBuilder sb = new StringBuilder();
    write.writeApply(sb);
    assertThat(sb.toString()).isEqualTo(apply);
    
    sb.setLength(0);
    write.writeDropAll(sb);
    assertThat(sb.toString()).isEqualTo(dropAll);
  }

  @Disabled
  @Test
  public void generateChangeSetFromModel() throws Exception {

    SpiEbeanServer defaultServer = (SpiEbeanServer) DB.getDefault();

    ChangeSet createChangeSet = new CurrentModel(defaultServer).getChangeSet();

    BaseDdlWrite write = new BaseDdlWrite();

    DdlHandler handler = h2Handler();
    handler.generate(write, createChangeSet);

    String apply = Helper.asText(this, "/assert/changeset-apply.txt");
    String rollbackLast = Helper.asText(this, "/assert/changeset-dropAll.txt");

    assertThat(write.toString()).isEqualTo(apply);
    //assertThat(write.dropWriter().toString()).isEqualTo(rollbackLast);
  }

  @Disabled
  @Test
  public void generateChangeSetFromModel_given_postgresTypes() throws Exception {
    SpiEbeanServer defaultServer = (SpiEbeanServer) DB.getDefault();

    ChangeSet createChangeSet = new CurrentModel(defaultServer).getChangeSet();

    BaseDdlWrite write = new BaseDdlWrite();

    DdlHandler handler = postgresHandler();
    handler.generate(write, createChangeSet);

    String apply = Helper.asText(this, "/assert/changeset-pg-apply.sql");
    String rollback = Helper.asText(this, "/assert/changeset-pg-rollback.sql");

    assertThat(write.toString()).isEqualTo(apply);
    //assertThat(write.dropWriter().toString()).isEqualTo(rollback);
  }

}
