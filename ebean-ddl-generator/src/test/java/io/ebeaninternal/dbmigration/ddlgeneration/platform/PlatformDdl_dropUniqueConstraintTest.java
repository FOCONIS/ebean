package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebean.config.dbplatform.hana.HanaPlatform;
import io.ebean.config.dbplatform.mysql.MySqlPlatform;
import io.ebean.config.dbplatform.oracle.OraclePlatform;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;
import io.ebean.config.dbplatform.sqlserver.SqlServer17Platform;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.PlatformDdlBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlatformDdl_dropUniqueConstraintTest {

  private final PlatformDdl h2Ddl = PlatformDdlBuilder.create(new H2Platform());
  private final PlatformDdl pgDdl = PlatformDdlBuilder.create(new PostgresPlatform());
  private final PlatformDdl mysqlDdl = PlatformDdlBuilder.create(new MySqlPlatform());
  private final PlatformDdl oraDdl = PlatformDdlBuilder.create(new OraclePlatform());
  private final PlatformDdl sqlServerDdl = PlatformDdlBuilder.create(new SqlServer17Platform());
  private final PlatformDdl hanaDdl = PlatformDdlBuilder.create(new HanaPlatform());

  String alterTableDropUniqueConstraint(PlatformDdl ddl) {
    BaseDdlWrite write = new BaseDdlWrite();
    ddl.alterTableDropUniqueConstraint(write, "mytab", "uq_name");
    return write.toString();
  }
  @Test
  public void test() throws Exception {

    String sql = alterTableDropUniqueConstraint(h2Ddl);
    assertEquals("-- altering tables\n"
        + "alter table mytab drop constraint uq_name;\n", sql);
    sql = alterTableDropUniqueConstraint(pgDdl);
    assertEquals("-- altering tables\n"
        + "alter table mytab drop constraint uq_name;\n", sql);
    sql = alterTableDropUniqueConstraint(oraDdl);
    assertEquals("-- altering tables\n"
        + "alter table mytab drop constraint uq_name;\n", sql);
    sql = alterTableDropUniqueConstraint(sqlServerDdl);
    assertEquals("-- indices/constraints\n"
        + "IF EXISTS (SELECT name FROM sys.indexes WHERE object_id = OBJECT_ID('mytab','U') AND name = 'uq_name') drop index uq_name ON mytab;\n"
        + "IF (OBJECT_ID('uq_name', 'UQ') IS NOT NULL) alter table mytab drop constraint uq_name;\n",
        sql);

    sql = alterTableDropUniqueConstraint(mysqlDdl);
    assertEquals("-- altering tables\n"
        + "alter table mytab drop index uq_name;\n", sql);

    DatabaseConfig config = new DatabaseConfig();
    hanaDdl.configure(config);
    sql = alterTableDropUniqueConstraint(hanaDdl);
    assertEquals("-- indices/constraints\n"
        + "delimiter $$\n"
        + "do\n"
        + "begin\n"
        + "declare exit handler for sql_error_code 397 begin end;\n"
        + "exec 'alter table mytab drop constraint uq_name';\n"
        + "end;\n"
        + "$$;\n", sql);
  }

}
