package io.ebeaninternal.server.type;

import io.ebean.config.JsonConfig;
import io.ebean.core.type.BasicTypeConverter;
import io.ebean.core.type.DataBinder;
import io.ebean.core.type.ScalarTypeBaseDateTime;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Calendar;

import static io.ebean.core.type.ScalarTypeUtils.formatInstant;

/**
 * ScalarType for java.util.Calendar.
 */
final class ScalarTypeCalendar extends ScalarTypeBaseDateTime<Calendar> {

  ScalarTypeCalendar(JsonConfig.DateTime mode, int jdbcType) {
    super(mode, Calendar.class, false, jdbcType);
  }

  @Override
  public java.util.Date bind(DataBinder binder, Calendar value) throws SQLException {
    if (value == null) {
      binder.setNull(Types.TIMESTAMP);
      return null;
    } else {
      if (jdbcType == Types.TIMESTAMP) {
        Timestamp timestamp = new Timestamp(value.getTimeInMillis());
        binder.setTimestamp(timestamp);
        return timestamp;
      } else {
        Date d = new Date(value.getTimeInMillis());
        binder.setDate(d);
        return d;
      }
    }
  }

  @Override
  public Calendar convertFromMillis(long systemTimeMillis) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(systemTimeMillis);
    return calendar;
  }

  @Override
  public Calendar convertFromTimestamp(Timestamp ts) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(ts.getTime());
    return calendar;
  }

  @Override
  public Calendar convertFromInstant(Instant ts) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(ts.toEpochMilli());
    return calendar;
  }

  @Override
  protected String toJsonNanos(Calendar value) {
    return String.valueOf(value.getTime().getTime());
  }

  @Override
  protected String toJsonISO8601(Calendar value) {
    return formatInstant(value.toInstant());
  }

  @Override
  public long convertToMillis(Calendar value) {
    return value.getTimeInMillis();
  }

  @Override
  public Timestamp convertToTimestamp(Calendar t) {
    return new Timestamp(t.getTimeInMillis());
  }

  @Override
  public Object toJdbcType(Object value) {
    return BasicTypeConverter.convert(value, jdbcType);
  }

  @Override
  public Calendar toBeanType(Object value) {
    return BasicTypeConverter.toCalendar(value);
  }

}
