package org.tests.timezone;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalTimeTest extends DatesAndTimesTest {

  @Test
  public void testLocalTime() {

    LocalTime lt = LocalTime.of(5, 15, 15);
    assertThat(db.find(MLocalTime.class).findCount()).isEqualTo(0);
    db.sqlUpdate("insert into mlocal_time (id, local_time) values (1, '05:15:15')").execute();

    MLocalTime dbModel = db.find(MLocalTime.class).where().eq("local_time", lt).findOne();
    assertThat(dbModel.getLocalTime().toString()).isEqualTo(lt.toString());

    int count = db.find(MLocalTime.class).where().eq("local_time", lt).findCount();
    assertThat(count).isEqualTo(1);

//    restartServer("PST", "Europe/Berlin");
//
//    assertThat(db.find(MLocalTime.class).findCount()).isEqualTo(0);
//    db.sqlUpdate("insert into mlocal_time (id, local_time) values (1, '05:15:15')").execute();
//
//    dbModel = db.find(MLocalTime.class).where().eq("local_time", lt).findOne();
//    assertThat(dbModel.getLocalTime().toString()).isEqualTo(lt.toString());
//
//    count = db.find(MLocalTime.class).where().eq("local_time", lt).findCount();
//    assertThat(count).isEqualTo(1);
//
//    restartServer("Europe/Berlin", "PST");
//
//    assertThat(db.find(MLocalTime.class).findCount()).isEqualTo(0);
//    db.sqlUpdate("insert into mlocal_time (id, local_time) values (1, '05:15:15')").execute();
//
//    dbModel = db.find(MLocalTime.class).where().eq("local_time", lt).findOne();
//    assertThat(dbModel.getLocalTime().toString()).isEqualTo(lt.toString());
//
//    count = db.find(MLocalTime.class).where().eq("local_time", lt).findCount();
//    assertThat(count).isEqualTo(1);
    
//      doTest("localTime", lt, String.valueOf(lt.toNanoOfDay()));
//      softly.assertThat(json).isEqualTo("{\"localTime\":\"05:15:15.123456789\"}");
//      softly.assertThat(formatted).isEqualTo("05:15:15.123456789");
//      return;
    // localTimes are never converted, when read or written to database
//    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
//    softly.assertThat(json).isEqualTo("{\"localTime\":\"05:15:15\"}");
//    softly.assertThat(formatted).isEqualTo("05:15:15");
//
//    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
//    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");
//
//    // it does not matter in which timezone the server or java is!
//    restartServer("PST", "Europe/Berlin");
//    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
//    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
//    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");
//
//    restartServer("Europe/Berlin", "PST");
//    doTest("localTime", LocalTime.of(5, 15, 15), "05:15:15");
//    doTest("localTime", LocalTime.of(0, 0, 0), "00:00:00");
//    doTest("localTime", LocalTime.of(23, 59, 59), "23:59:59");

  }
}
