package io.ebeaninternal.server.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.ModelWithDateTimeProperties;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;

public class DatesAndTimesTest extends BaseTestCase {
  
  private Database createServer() {

    DatabaseConfig config = new DatabaseConfig();
    config.setName("h2other");
    config.loadFromProperties();
    config.setDdlGenerate(true);
    config.setDdlRun(true);
    config.setDdlExtra(false);
    config.setDefaultServer(false);
    config.setRegister(false);
    config.setChangeLogAsync(false);
    
    config.setDataTimeZone("GMT");

    return DatabaseFactory.create(config);
  }
  
  @Test
  public void testLocalTime() {

    Database db = createServer();
    
    String rawSql = "insert into model_with_date_time_properties (id, local_time) values (1, '05:15:15')";
    int count = db.sqlUpdate(rawSql).execute();
    assertEquals(count, 1);
    
    List<LocalTime> list = db.find(ModelWithDateTimeProperties.class).select("localTime").findSingleAttributeList();
    assertEquals(list.size(), 1);
    assertEquals(list.get(0).getHour(), 5);
    assertEquals(list.get(0).getMinute(), 15);
    assertEquals(list.get(0).getSecond(), 15);
  }
  
  @Test
  public void testLocalDateTime() {

    Database db = createServer();
    
    String rawSql = "insert into model_with_date_time_properties (id, local_date_time) values (1, '2021-11-22 05:15:15')";
    int count = db.sqlUpdate(rawSql).execute();
    assertEquals(count, 1);
    
    List<LocalDateTime> list = db.find(ModelWithDateTimeProperties.class).select("localDateTime").findSingleAttributeList();
    assertEquals(list.size(), 1);
    assertEquals(list.get(0).getYear(), 2021);
    assertEquals(list.get(0).getMonth(), Month.NOVEMBER);
    assertEquals(list.get(0).getDayOfMonth(), 22);
    assertEquals(list.get(0).getHour(), 5);
    assertEquals(list.get(0).getMinute(), 15);
    assertEquals(list.get(0).getSecond(), 15);
  }
  
  @Test
  public void testLocalDate() {

    Database db = createServer();
    
    String rawSql = "insert into model_with_date_time_properties (id, local_date) values (1, '2021-11-22')";
    int count = db.sqlUpdate(rawSql).execute();
    assertEquals(count, 1);
    
    List<LocalDate> list = db.find(ModelWithDateTimeProperties.class).select("localDate").findSingleAttributeList();
    assertEquals(list.size(), 1);
    assertEquals(list.get(0).getYear(), 2021);
    assertEquals(list.get(0).getMonth(), Month.NOVEMBER);
    assertEquals(list.get(0).getDayOfMonth(), 22);
  }
  
  @AfterEach
  public void resetData() {
    DB.find(ModelWithDateTimeProperties.class).delete();
  }

}
