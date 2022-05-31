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

 // @BeforeEach
  @BeforeAll
  public void startTest() {
  //  tz = TimeZone.getDefault();
   // if (db == null) {
      db = createServer("GMT", null, null); // test uses GMT database
//    } else {
//      restartServer(null, "GMT");
//    }
  }

//  @AfterEach
//  public void stopTest() {
//    setJavaTimeZone(tz);
//  }

  @AfterAll
  public void shutdown() {
    if (db != null) {
      db.find(MLocalTime.class).delete();
      db.shutdown();
    }
  }

//  protected void restartServer(String javaTimeZone, String dbTimeZone) {
//    DataSource existingDs = db.dataSource();
//    DataSource existingRoDs = db.readOnlyDataSource();
//    db.shutdown(false, false);
//    if (javaTimeZone != null) {
//      setJavaTimeZone(TimeZone.getTimeZone(javaTimeZone));
//    }
//    db = createServer(dbTimeZone, existingDs, existingRoDs);
//  }

//  private void setJavaTimeZone(TimeZone newTz) {
//    TimeZone.setDefault(newTz);
//    DateTimeZone.setDefault(DateTimeZone.forTimeZone(newTz));
//    org.h2.util.DateTimeUtils.resetCalendar();
//  }

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
   // reconfigure(config);

    return DatabaseFactory.create(config);
  }

//  protected void reconfigure(DatabaseConfig config) {
//
//  }

}
