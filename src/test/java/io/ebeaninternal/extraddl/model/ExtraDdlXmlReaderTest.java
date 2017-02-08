package io.ebeaninternal.extraddl.model;

import org.junit.Test;

import io.ebean.dbmigration.DbSchemaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class ExtraDdlXmlReaderTest {

  @Test
  public void read() throws Exception {

    ExtraDdl read = ExtraDdlXmlReader.read("/extra-ddl.xml");
    assertNotNull(read);
  }

  @Test
  public void buildExtra_when_h2() {

    String ddl = ExtraDdlXmlReader.buildExtra("h2", DbSchemaType.ALL);

    assertThat(ddl).contains("create or replace view ${tenant_schema}.order_agg_vw");
    assertThat(ddl).contains("-- h2 and postgres script");
    assertThat(ddl).doesNotContain(" -- oracle only script");

    ddl = ExtraDdlXmlReader.buildExtra("h2", DbSchemaType.TENANT);
    assertThat(ddl).contains("create or replace view ${tenant_schema}.order_agg_vw");

    ddl = ExtraDdlXmlReader.buildExtra("h2", DbSchemaType.SHARED);
    assertThat(ddl).doesNotContain("create or replace view ${tenant_schema}.order_agg_vw");
  }

  @Test
  public void buildExtra_when_oracle() {

    String ddl = ExtraDdlXmlReader.buildExtra("oracle", DbSchemaType.ALL);

    assertThat(ddl).contains("create or replace view ${tenant_schema}.order_agg_vw");
    assertThat(ddl).doesNotContain("-- h2 and postgres script");
    assertThat(ddl).contains(" -- oracle only script");
  }

  @Test
  public void buildExtra_when_mysql() {

    String ddl = ExtraDdlXmlReader.buildExtra("mysql", DbSchemaType.ALL);

    assertThat(ddl).contains("create or replace view ${tenant_schema}.order_agg_vw");
    assertThat(ddl).doesNotContain("-- h2 and postgres script");
    assertThat(ddl).doesNotContain(" -- oracle only script");
  }

}
