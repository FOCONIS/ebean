package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.dbplatform.hana.HanaPlatform;
import io.ebeaninternal.dbmigration.ddlgeneration.BaseDdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.migration.Column;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HanaDdlTest {

  @Test
  public void alterTableDropColumn() throws IOException {
    HanaColumnStoreDdl ddl = new HanaColumnStoreDdl(new HanaPlatform());
    DdlWrite write = new BaseDdlWrite();
    ddl.alterTableDropColumn(write, "my_table", "my_column", false);
    assertEquals("-- apply changes\nCALL usp_ebean_drop_column('my_table', 'my_column');\n", write.toString());
  }

  @Test
  public void alterTableAddColumn() throws IOException {
    HanaColumnStoreDdl ddl = new HanaColumnStoreDdl(new HanaPlatform());
    DdlWrite write = new BaseDdlWrite();
    Column column = new Column();
    column.setName("my_column");
    column.setComment("comment");
    column.setDefaultValue("1");
    column.setNotnull(Boolean.TRUE);
    column.setType("int");
    column.setUnique("unique");
    column.setPrimaryKey(Boolean.TRUE);
    column.setCheckConstraint("CHECK(my_column > 0)");
    column.setCheckConstraintName("check_constraint");
    column.setHistoryExclude(Boolean.TRUE);
    column.setIdentity(Boolean.TRUE);
    ddl.alterTableAddColumn(write, "my_table", column, false, "1");
    assertEquals("-- altering tables\n"
        + "alter table my_table add ( my_column int default 1 not null);\n"
        + "alter table my_table add constraint check_constraint CHECK(my_column > 0);\n", write.toString());
  }
}
