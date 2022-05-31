package org.tests.timezone;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalTimeTest {

  protected String platform="h2";
  protected Database db;

  @BeforeAll
  public void startTest() {
    db = createServer("GMT", null, null); // test uses GMT database
  }

  @AfterAll
  public void shutdown() {
    if (db != null) {
      db.find(MLocalTime.class).delete();
      db.shutdown();
    }
  }

  /**
   * The test checks the write and read of LocalTime values. The database is in GMT time zone.
   * In order to verify the test in different java time zones (where the application runs),
   * use the <code>-Duser.timezone</code> as JVM argument,
   * e.g. <code>-Duser.timezone="America/New_York"</code> or <code>-Duser.timezone="PST"</code>>.
   */
  @Test
  public void testLocalTime() {
    LocalTime lt = LocalTime.of(5, 15, 15);
    assertThat(db.find(MLocalTime.class).findCount()).isEqualTo(0);
    db.sqlUpdate("insert into mlocal_time (id, local_time) values (1, '05:15:15')").execute();

    int count = db.find(MLocalTime.class).where().eq("local_time", lt).findCount();
    assertThat(count).isEqualTo(1);

    MLocalTime dbModel = db.find(MLocalTime.class).where().eq("local_time", lt).findOne();
    assertThat(dbModel.getLocalTime().toString()).isEqualTo(lt.toString());
  }

  private Database createServer(String dbTimeZone, DataSource existingDs, DataSource existingRoDs) {
    DatabaseConfig config = new DatabaseConfig();
    config.setName(platform);
    config.loadFromProperties();
    config.setDdlGenerate(existingDs == null );
    config.setDdlRun(existingDs == null);
    config.setReadOnlyDataSource(existingDs);
    config.setDdlExtra(false);
    config.setDefaultServer(false);
    config.setRegister(false);
    config.setChangeLogAsync(false);
    config.addClass(MLocalTime.class);

    config.setDumpMetricsOnShutdown(false);
    config.setDataTimeZone(dbTimeZone);
    config.setDataSource(existingDs);

    return DatabaseFactory.create(config);
  }
}
