package org.tests.timezone;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.text.PathProperties;
import io.ebean.text.json.JsonWriteOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DateTimeJsonISOTest {

  protected String platform = "h2";
  protected Database db;

  @BeforeAll
  public void startTest() {
    db = createServer("GMT"); // test uses GMT database
  }

  @AfterAll
  public void shutdown() {
    if (db != null) {
      db.find(MLocalDate.class).delete();
      db.shutdown();
    }
  }

  @Test
  public void testLocalDateTimeWithJson() {
    LocalDateTime ldt = LocalDateTime.parse("2021-11-21T05:15:15");
    assertThat(db.find(MLocalDateTime.class).findCount()).isEqualTo(0);
    db.sqlUpdate("insert into mlocal_date_time (id, local_date_time) values (1, '2021-11-21 05:15:15')").execute();

   // MLocalDateTime model = db.find(MLocalDateTime.class).where().eq("local_date_time", ldt).findOne();
    MLocalDateTime model = db.find(MLocalDateTime.class).findOne();

    JsonWriteOptions opts = new JsonWriteOptions();
    opts.setPathProperties(PathProperties.parse("localDateTime"));
    String json = db.json().toJson(model, opts);

    assertThat(json).isEqualTo("{\"localDateTime\":\"2021-11-21T05:15:15\"}");

    model = db.json().toBean(MLocalDateTime.class, json);
    assertThat(model.getLocalDateTime()).isEqualTo(ldt);
  }

  private Database createServer(String dbTimeZone) {
    DatabaseConfig config = new DatabaseConfig();
    config.setName(platform);
    config.loadFromProperties();
    config.setDdlExtra(false);
    config.setDefaultServer(false);
    config.setRegister(false);
    config.setChangeLogAsync(false);
    config.addClass(MLocalDateTime.class);

    config.setDumpMetricsOnShutdown(false);
    config.setDataTimeZone(dbTimeZone);

//    config.setJsonDate(io.ebean.config.JsonConfig.Date.MILLIS);
//    config.setJsonDateTime(io.ebean.config.JsonConfig.DateTime.MILLIS);

    return DatabaseFactory.create(config);
  }

}
