package io.ebeaninternal.server.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.MDateTime;

import io.ebean.BaseTestCase;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.mariadb.MariaDbPlatform;
import io.ebean.config.dbplatform.sqlserver.SqlServer17Platform;
import io.ebean.plugin.ExpressionPath;
import io.ebean.plugin.Property;
import io.ebean.util.CamelCaseHelper;
import io.ebeaninternal.server.deploy.BeanProperty;

public class DatesAndTimesTest  {

  private Database db;
  private TimeZone tz;

  @BeforeEach
  public void setup() {
    tz = TimeZone.getDefault();
    db = createServer("GMT"); // test uses GMT database
  }

  @AfterEach
  public void shutdown() {
    db.find(MDateTime.class).delete();
    db.shutdown();
    setJavaTimeZone(tz);
  }

  private void restartServer(String javaTimeZone, String dbTimeZone) {
    db.shutdown();
    setJavaTimeZone(TimeZone.getTimeZone(javaTimeZone));
    db = createServer(dbTimeZone);
  }

  private void setJavaTimeZone(TimeZone newTz) {
    TimeZone.setDefault(newTz);
    // set also joda TZ info
    DateTimeZone.setDefault(DateTimeZone.forTimeZone(newTz));
    org.h2.util.DateTimeUtils.resetCalendar();
  }
  
  private Database createServer(String dbTimeZone) {

    DatabaseConfig config = new DatabaseConfig();
    config.setName("h2other");
    config.loadFromProperties();
    config.setDdlGenerate(true);
    config.setDdlRun(true);
    config.setDdlExtra(false);
    config.setDefaultServer(false);
    config.setRegister(false);
    config.setChangeLogAsync(false);
    config.addClass(MDateTime.class);
    
    //config.setLocalTimeWithNanos(true);

    // no matter what timezones are set. LocalDate / LocalDateTime and LocalTime are never converted
    config.setDataTimeZone(dbTimeZone);
    

    // Mariadb
    // config.setDdlCreateOnly(false);
    // config.setDdlRun(false);
    // config.setName("mariadb-docker04");
    // config.setDatabasePlatform(new MariaDbPlatform());
    //
    // config.getDataSourceConfig().setUrl("jdbc:mariadb://srv-01-docker04.foconis.local:3306/zak_szemenyei_1");
    // config.getDataSourceConfig().setDriver("org.mariadb.jdbc.Driver");
    // config.getDataSourceConfig().setUsername("tenant1user");
    // config.getDataSourceConfig().setPassword("tenant1pw");
    // config.getDataSourceConfig().setInitSql(Arrays.asList("SET NAMES utf8mb4",
    // "SET collation_connection = 'utf8mb4_bin'"));

    // SqlServer
    // config.setDdlCreateOnly(false);
    // config.setDdlRun(false);
    // config.setName("mssql");
    // config.setDatabasePlatform(new SqlServer17Platform());
    //
    // config.getDataSourceConfig().setUrl("jdbc:sqlserver://srv-01-docker02.foconis.local:1433;databaseName=zak_szemenyei_1;sendTimeAsDatetime=false");
    // config.getDataSourceConfig().setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    // config.getDataSourceConfig().setUsername("tenant1user");
    // config.getDataSourceConfig().setPassword("tenant1pw");

    return DatabaseFactory.create(config);
  }

  @Test
  public void testLocalTime() {
    // localTimes are never converted, when read or written to database
    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");

    // it does not matter in which timezone the server or java is!
    restartServer("PST", "Europe/Berlin");
    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");

    restartServer("Europe/Berlin", "PST");
    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");

  }

  @Test
  public void testJodaLocalTime() {
    // localTimes are never converted, when read or written to database
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("05:15:15"), "05:15:15");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("00:00:00"), "00:00:00");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("23:59:59"), "23:59:59");

    // it does not matter in which timezone the server or java is!
    restartServer("PST", "Europe/Berlin");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("05:15:15"), "05:15:15");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("00:00:00"), "00:00:00");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("23:59:59"), "23:59:59");

    restartServer("Europe/Berlin", "PST");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("05:15:15"), "05:15:15");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("00:00:00"), "00:00:00");
    doTest("jodaLocalTime", org.joda.time.LocalTime.parse("23:59:59"), "23:59:59");

  }

  @Test
  public void testLocalDate() {

    // Test with DST and no DST date (in germany)
    doTest("localDate", LocalDate.parse("2021-11-21"), "2021-11-21");
    doTest("localDate", LocalDate.parse("2021-08-21"), "2021-08-21");

    restartServer("PST", "Europe/Berlin");
    doTest("localDate", LocalDate.parse("2021-11-21"), "2021-11-21");
    doTest("localDate", LocalDate.parse("2021-08-21"), "2021-08-21");

    restartServer("Europe/Berlin", "PST");
    doTest("localDate", LocalDate.parse("2021-11-21"), "2021-11-21");
    doTest("localDate", LocalDate.parse("2021-08-21"), "2021-08-21");

  }

  @Test
  public void testJodaLocalDate() {

    // Test with DST and no DST date (in germany)
    doTest("jodaLocalDate", org.joda.time.LocalDate.parse("2021-11-21"), "2021-11-21");
    doTest("jodaLocalDate", org.joda.time.LocalDate.parse("2021-08-21"), "2021-08-21");

    restartServer("PST", "Europe/Berlin");
    doTest("jodaLocalDate", org.joda.time.LocalDate.parse("2021-11-21"), "2021-11-21");
    doTest("jodaLocalDate", org.joda.time.LocalDate.parse("2021-08-21"), "2021-08-21");

    restartServer("Europe/Berlin", "PST");
    doTest("jodaLocalDate", org.joda.time.LocalDate.parse("2021-11-21"), "2021-11-21");
    doTest("jodaLocalDate", org.joda.time.LocalDate.parse("2021-08-21"), "2021-08-21");

  }

  @Test
  public void testCalendar() {

    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(0); // clear
    cal.set(2021, 8 - 1, 21, 5, 15, 15); // month 0-based!

    assertThat(cal.toInstant()).isEqualTo(Instant.parse("2021-08-21T05:15:15Z"));

    System.out.println(cal.toInstant());
    doTest("calendar", cal, "2021-08-21 05:15:15");

    // test in PST time zone

    restartServer("PST", "GMT");
    cal = Calendar.getInstance();
    cal.setTimeInMillis(0); // clear
    cal.set(2021, 8 - 1, 20, 22, 15, 15); // month 0-based!

    assertThat(cal.toInstant()).isEqualTo(Instant.parse("2021-08-21T05:15:15Z"));

    doTest("calendar", cal, "2021-08-21 05:15:15");
  }

  @Test
  public void testInstant() {

    // Test with DST and no DST date (in germany)
    doTest("instant", Instant.parse("2021-11-21T05:15:15Z"), "2021-11-21 05:15:15");
    doTest("instant", Instant.parse("2021-08-21T05:15:15Z"), "2021-08-21 05:15:15");

    restartServer("PST", "GMT");

    doTest("instant", Instant.parse("2021-11-21T05:15:15Z"), "2021-11-21 05:15:15");
    doTest("instant", Instant.parse("2021-08-21T05:15:15Z"), "2021-08-21 05:15:15");
  }

  @Test
  public void testJodaDateTime() {

    // Test with DST and no DST date (in germany)
    doTest("jodaDateTime", org.joda.time.DateTime.parse("2021-11-21T05:15:15Z"), "2021-11-21 05:15:15");
    doTest("jodaDateTime", org.joda.time.DateTime.parse("2021-08-21T05:15:15Z"), "2021-08-21 05:15:15");

    restartServer("PST", "GMT");

    doTest("jodaDateTime", org.joda.time.DateTime.parse("2021-11-21T05:15:15Z"), "2021-11-21 05:15:15");
    doTest("jodaDateTime", org.joda.time.DateTime.parse("2021-08-21T05:15:15Z"), "2021-08-21 05:15:15");
  }

  @Test
  public void testLocalDateTime() {

    // Test with DST and no DST date (in germany)
    doTest("localDateTime", LocalDateTime.parse("2021-11-21T05:15:15"), "2021-11-21 05:15:15");
    doTest("localDateTime", LocalDateTime.parse("2021-08-21T05:15:15"), "2021-08-21 05:15:15");

    restartServer("PST", "Europe/Berlin");
    doTest("localDateTime", LocalDateTime.parse("2021-11-21T05:15:15"), "2021-11-21 05:15:15");
    doTest("localDateTime", LocalDateTime.parse("2021-08-21T05:15:15"), "2021-08-21 05:15:15");

    restartServer("Europe/Berlin", "PST");
    doTest("localDateTime", LocalDateTime.parse("2021-11-21T05:15:15"), "2021-11-21 05:15:15");
    doTest("localDateTime", LocalDateTime.parse("2021-08-21T05:15:15"), "2021-08-21 05:15:15");

  }

  @Test
  public void testJodaLocalDateTime() {

    // Test with DST and no DST date (in germany)
    doTest("jodaLocalDateTime", org.joda.time.LocalDateTime.parse("2021-11-21T05:15:15"), "2021-11-21 05:15:15");
    doTest("jodaLocalDateTime", org.joda.time.LocalDateTime.parse("2021-08-21T05:15:15"), "2021-08-21 05:15:15");

    restartServer("PST", "Europe/Berlin");
    doTest("jodaLocalDateTime", org.joda.time.LocalDateTime.parse("2021-11-21T05:15:15"), "2021-11-21 05:15:15");
    doTest("jodaLocalDateTime", org.joda.time.LocalDateTime.parse("2021-08-21T05:15:15"), "2021-08-21 05:15:15");

    restartServer("Europe/Berlin", "PST");
    doTest("jodaLocalDateTime", org.joda.time.LocalDateTime.parse("2021-11-21T05:15:15"), "2021-11-21 05:15:15");
    doTest("jodaLocalDateTime", org.joda.time.LocalDateTime.parse("2021-08-21T05:15:15"), "2021-08-21 05:15:15");

  }

  @Test
  public void testJodaDateMidnight() {

    // Test with DST and no DST date (in germany)
    doTest("jodaDateMidnight", org.joda.time.DateMidnight.parse("2021-11-21"), "2021-11-21");
    doTest("jodaDateMidnight", org.joda.time.DateMidnight.parse("2021-08-21"), "2021-08-21");

    restartServer("PST", "Europe/Berlin");
    doTest("jodaDateMidnight", org.joda.time.DateMidnight.parse("2021-11-21"), "2021-11-21");
    doTest("jodaDateMidnight", org.joda.time.DateMidnight.parse("2021-08-21"), "2021-08-21");

    restartServer("Europe/Berlin", "PST");
    doTest("jodaDateMidnight", org.joda.time.DateMidnight.parse("2021-11-21"), "2021-11-21");
    doTest("jodaDateMidnight", org.joda.time.DateMidnight.parse("2021-08-21"), "2021-08-21");
  }

  @Test
  public void testYearMonth() {

    // Test with DST and no DST date (in germany)
    doTest("yearMonth", YearMonth.of(2020, 2), "2020-02-01");
    doTest("yearMonth", YearMonth.of(2020, 3), "2020-03-01");
    doTest("yearMonth", YearMonth.of(2020, 4), "2020-04-01");

  }
  @Test
  public void testYear() {

    doTest("year", Year.of(2020), "2020");

  }
  
  @Test
  public void testMonthDay() {

    // Test with DST and no DST date (in germany)
    doTest("monthDay", MonthDay.of(11, 21), "2000-11-21");
    doTest("monthDay", MonthDay.of(8, 21), "2000-08-21");
    doTest("monthDay", MonthDay.of(2, 29), "2000-02-29"); // leap year check

    restartServer("PST", "Europe/Berlin");
    doTest("monthDay", MonthDay.of(11, 21), "2000-11-21");
    doTest("monthDay", MonthDay.of(8, 21), "2000-08-21");
    doTest("monthDay", MonthDay.of(2, 29), "2000-02-29"); // leap year check

    restartServer("Europe/Berlin", "PST");
    doTest("monthDay", MonthDay.of(11, 21), "2000-11-21");
    doTest("monthDay", MonthDay.of(8, 21), "2000-08-21");
    doTest("monthDay", MonthDay.of(2, 29), "2000-02-29"); // leap year check
  }

  @Test
  public void testSqlDate() {
    // localTimes are never converted, when read or written to database
    doTest("sqlDate", new java.sql.Date(2021 - 1900, 11 - 1, 21), "2021-11-21");
    doTest("sqlDate", new java.sql.Date(2021 - 1900, 8 - 1, 21), "2021-08-21");

    // it does not matter in which timezone the server or java is!
    restartServer("PST", "Europe/Berlin");
    doTest("sqlDate", new java.sql.Date(2021 - 1900, 11 - 1, 21), "2021-11-21");
    doTest("sqlDate", new java.sql.Date(2021 - 1900, 8 - 1, 21), "2021-08-21");

    restartServer("Europe/Berlin", "PST");
    doTest("sqlDate", new java.sql.Date(2021 - 1900, 11 - 1, 21), "2021-11-21");
    doTest("sqlDate", new java.sql.Date(2021 - 1900, 8 - 1, 21), "2021-08-21");

  }

  @Test
  public void testSqlTime() {

    // Test with DST and no DST date (in germany)
    doTest("sqlTime", new java.sql.Time(5, 15, 15), "05:15:15");
    doTest("sqlTime", new java.sql.Time(0, 0, 0), "00:00:00");
    doTest("sqlTime", new java.sql.Time(23, 59, 59), "23:59:59");

    restartServer("PST", "Europe/Berlin");
    doTest("sqlTime", new java.sql.Time(5, 15, 15), "05:15:15");
    doTest("sqlTime", new java.sql.Time(0, 0, 0), "00:00:00");
    doTest("sqlTime", new java.sql.Time(23, 59, 59), "23:59:59");

    restartServer("Europe/Berlin", "PST");
    doTest("sqlTime", new java.sql.Time(5, 15, 15), "05:15:15");
    doTest("sqlTime", new java.sql.Time(0, 0, 0), "00:00:00");
    doTest("sqlTime", new java.sql.Time(23, 59, 59), "23:59:59");
  }
  
  @Test
  public void testTimestamp() {
    restartServer("PST", "PST"); // java & db in same TZ
    doTest("timestamp", new Timestamp(2021 - 1900, 11 - 1, 21, 5, 15, 15, 0), "2021-11-21 05:15:15");
    
    restartServer("Europe/Berlin", "GMT"); // go to germany
    doTest("timestamp", new Timestamp(2021 - 1900, 11 - 1, 21, 6, 15, 15, 0), "2021-11-21 05:15:15");
    doTest("timestamp", new Timestamp(2021 - 1900, 8 - 1, 21, 7, 15, 15, 0), "2021-08-21 05:15:15"); // Check DST
    
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 27, 23, 0, 0, 0), "2021-03-27 22:00:00");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 28, 0, 0, 0, 0), "2021-03-27 23:00:00");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 28, 1, 0, 0, 0), "2021-03-28 00:00:00");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 28, 2, 0, 0, 0), "2021-03-28 01:00:00");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 28, 3, 0, 1, 0), "2021-03-28 01:00:01");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 28, 4, 0, 0, 0), "2021-03-28 02:00:00");

    restartServer("GMT", "Europe/Berlin"); 
    doTest("timestamp", new Timestamp(2021 - 1900, 11 - 1, 21, 4, 15, 15, 0), "2021-11-21 05:15:15");
    doTest("timestamp", new Timestamp(2021 - 1900, 8 - 1, 21, 3, 15, 15, 0), "2021-08-21 05:15:15"); // Check DST
    
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 27, 1, 0, 0, 0), "2021-03-27 02:00:00");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 27, 23, 0, 0, 0), "2021-03-28 00:00:00");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 28, 0, 0, 0, 0), "2021-03-28 01:00:00");
    doTest("timestamp", new Timestamp(2021 - 1900, 3 - 1, 28, 1, 0, 0, 0), "2021-03-28 03:00:00");
  }

  @Test
  public void testUtilDate() {
    restartServer("PST", "PST"); // java & db in same TZ
    doTest("utilDate", new java.util.Date(2021 - 1900, 11 - 1, 21, 5, 15, 15), "2021-11-21 05:15:15");
    
    restartServer("Europe/Berlin", "GMT"); // go to germany
    doTest("utilDate", new java.util.Date(2021 - 1900, 11 - 1, 21, 6, 15, 15), "2021-11-21 05:15:15");
    doTest("utilDate", new java.util.Date(2021 - 1900, 8 - 1, 21, 7, 15, 15), "2021-08-21 05:15:15");
    
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 27, 23, 0, 0), "2021-03-27 22:00:00");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 28, 0, 0, 0), "2021-03-27 23:00:00");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 28, 1, 0, 0), "2021-03-28 00:00:00");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 28, 2, 0, 0), "2021-03-28 01:00:00");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 28, 3, 0, 1), "2021-03-28 01:00:01");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 28, 4, 0, 0), "2021-03-28 02:00:00");

    restartServer("GMT", "Europe/Berlin"); 
    doTest("utilDate", new java.util.Date(2021 - 1900, 11 - 1, 21, 4, 15, 15), "2021-11-21 05:15:15");
    doTest("utilDate", new java.util.Date(2021 - 1900, 8 - 1, 21, 3, 15, 15), "2021-08-21 05:15:15");
    
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 27, 1, 0, 0), "2021-03-27 02:00:00");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 27, 23, 0, 0), "2021-03-28 00:00:00");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 28, 0, 0, 0), "2021-03-28 01:00:00");
    doTest("utilDate", new java.util.Date(2021 - 1900, 3 - 1, 28, 1, 0, 0), "2021-03-28 03:00:00");
  }
  
  @Test
  public void testOffsetDateTime() {

    restartServer("PST", "PST"); // be in the same TZ
    doTest("offsetDateTime", OffsetDateTime.parse("2021-11-20T21:15:15-08:00"), "2021-11-20 21:15:15");

    restartServer("PST", "GMT"); // pass PST offsetDateTime to GMT DB
    doTest("offsetDateTime", OffsetDateTime.parse("2021-11-20T21:15:15-08:00"), "2021-11-21 05:15:15");
    
  }

  @Test
  public void testZonedDateTime() {

    restartServer("PST", "PST"); // be in the same TZ
    doTest("zonedDateTime", ZonedDateTime.parse("2021-11-20T21:15:15-08:00"), "2021-11-20 21:15:15");

    restartServer("PST", "GMT"); // pass PST offsetDateTime to GMT DB
    doTest("zonedDateTime", ZonedDateTime.parse("2021-11-20T21:15:15-08:00"), "2021-11-21 05:15:15");
    
  }
//  offsetDateTime : OffsetDateTime
//  zonedDateTime : ZonedDateTime
  private <T extends Comparable<? super T>> void doTest(String property,T javaValue, String sqlValue ) {
    db.find(MDateTime.class).delete(); // clear database
    String sqlColumn  = CamelCaseHelper.toUnderscoreFromCamel(property);
    // insert with raw sql
    db.sqlUpdate("insert into mdate_time (id, "+sqlColumn+") values (1, '" + sqlValue +"')").execute();
    
    // check findSingleAttributeList
    List<T> list = db.find(MDateTime.class)
        .select(property)
        .findSingleAttributeList();
    assertThat(list).hasSize(1);
    T attr = list.get(0);
  
    
    // check find model
    MDateTime model = db.find(MDateTime.class).where().eq(property, javaValue).findOne();
    Property beanProp = db.pluginApi().beanType(MDateTime.class).property(property);
    assertThat(model).isNotNull();
    @SuppressWarnings("unchecked")
    T beanValue = (T) beanProp.value(model);
    assertTimeEquals(beanValue, javaValue);
    
    // insert with "save"
    model = new MDateTime();
    model.setId(2);
    ((ExpressionPath)beanProp).pathSet(model, javaValue);
    db.save(model);
    
    // check findCount
    int count = db.find(MDateTime.class).where().eq(property, javaValue).findCount();
    assertThat(count).isEqualTo(2);
  }

  private <T extends Comparable<? super T>> void assertTimeEquals(T value, T expected) {
    if (value instanceof OffsetDateTime) {
      assertThat((OffsetDateTime) value).isAtSameInstantAs((OffsetDateTime) expected);
    } else if (value instanceof ZonedDateTime) {
      assertThat(((ZonedDateTime) value).toInstant()).isEqualTo(((ZonedDateTime) expected).toInstant());
    } else {
      assertThat(value).isEqualByComparingTo(expected);
    }
  }

}
