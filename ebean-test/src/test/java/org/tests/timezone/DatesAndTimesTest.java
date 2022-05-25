package org.tests.timezone;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.time.LocalTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatesAndTimesTest{

  protected String platform="h2";

  protected Database db;
  protected TimeZone tz;
  protected DatabaseConfig config;

  @BeforeEach
  public void startTest() {
    tz = TimeZone.getDefault();
    if (db == null) {
      db = createServer("GMT", null, null); // test uses GMT database
    } else {
      restartServer(null, "GMT");
    }
  }

  @AfterEach
  public void stopTest() {
    setJavaTimeZone(tz);
  }

  @AfterAll
  public void shutdown() {
    if (db != null) {
      db.find(MLocalTime.class).delete();
      db.shutdown();
    }
  }

  protected void restartServer(String javaTimeZone, String dbTimeZone) {
    DataSource existingDs = db.dataSource();
    DataSource existingRoDs = db.readOnlyDataSource();
    db.shutdown(false, false);
    if (javaTimeZone != null) {
      setJavaTimeZone(TimeZone.getTimeZone(javaTimeZone));
    }
    db = createServer(dbTimeZone, existingDs, existingRoDs);
  }

  private void setJavaTimeZone(TimeZone newTz) {
    TimeZone.setDefault(newTz);
    DateTimeZone.setDefault(DateTimeZone.forTimeZone(newTz));
    org.h2.util.DateTimeUtils.resetCalendar();
  }

  private Database createServer(String dbTimeZone, DataSource existingDs, DataSource existingRoDs) {

    config = new DatabaseConfig();
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
    reconfigure(config);

    return DatabaseFactory.create(config);
  }

  protected void reconfigure(DatabaseConfig config) {

  }

//  @Test
//  public void testLocalTime() {
//
//      LocalTime lt = LocalTime.of(5, 15, 15);
//
//      assertThat(db.find(MLocalTime.class).findCount()).isEqualTo(0);
//
//    db.sqlUpdate("insert into mlocal_time (id, local_time) values (1, '05:15:15')").execute();
//
//      MLocalTime dbModel = db.find(MLocalTime.class).where().eq("local_time", lt).findOne();
//      assertThat(dbModel.getLocalTime().toString()).isEqualTo(lt.toString());
//
//      int count = db.find(MLocalTime.class).where().eq("local_time", lt).findCount();
//      assertThat(count).isEqualTo(1);
//
//      // TODO: server mit verschiedenen TZen einbauen...
//
////      doTest("localTime", lt, String.valueOf(lt.toNanoOfDay()));
////      softly.assertThat(json).isEqualTo("{\"localTime\":\"05:15:15.123456789\"}");
////      softly.assertThat(formatted).isEqualTo("05:15:15.123456789");
////      return;
//    // localTimes are never converted, when read or written to database
////    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
////    softly.assertThat(json).isEqualTo("{\"localTime\":\"05:15:15\"}");
////    softly.assertThat(formatted).isEqualTo("05:15:15");
////
////    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
////    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");
////
////    // it does not matter in which timezone the server or java is!
////    restartServer("PST", "Europe/Berlin");
////    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
////    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
////    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");
////
////    restartServer("Europe/Berlin", "PST");
////    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
////    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
////    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");
//
//  }

}
