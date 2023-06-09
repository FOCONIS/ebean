package io.ebeaninternal.server.type;

import io.ebean.config.JsonConfig;
import io.ebean.core.type.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Date;

import static io.ebean.core.type.ScalarTypeUtils.formatInstant;

/**
 * ScalarType for java.util.Date.
 */
final class ScalarTypeUtilDate {

  static final class TimestampType extends ScalarTypeBaseDateTime<Date> {

    TimestampType(JsonConfig.DateTime mode) {
      super(mode, java.util.Date.class, false, Types.TIMESTAMP);
    }

    @Override
    protected String toJsonNanos(Date value) {
      return String.valueOf(value.getTime());
    }

    @Override
    protected String toJsonISO8601(Date value) {
      return formatInstant(value.toInstant());
    }

    @Override
    public long convertToMillis(Date value) {
      return value.getTime();
    }

    @Override
    public java.util.Date read(DataReader reader) throws SQLException {
      Timestamp timestamp = reader.getTimestamp();
      if (timestamp == null) {
        return null;
      } else {
        return new java.util.Date(timestamp.getTime());
      }
    }

    @Override
    public Timestamp bind(DataBinder binder, java.util.Date value) throws SQLException {
      if (value == null) {
        binder.setNull(Types.TIMESTAMP);
        return null;
      } else {
        Timestamp rawValue = new Timestamp(value.getTime());
        binder.setTimestamp(rawValue);
        return rawValue;
      }
    }

    @Override
    public Object toJdbcType(Object value) {
      return BasicTypeConverter.toTimestamp(value);
    }

    @Override
    public java.util.Date toBeanType(Object value) {
      return BasicTypeConverter.toUtilDate(value);
    }

    @Override
    public Date convertFromTimestamp(Timestamp ts) {
      return new java.util.Date(ts.getTime());
    }

    @Override
    public Date convertFromInstant(Instant ts) {
      return new java.util.Date(ts.toEpochMilli());
    }

    @Override
    public Timestamp convertToTimestamp(Date date) {
      return new Timestamp(date.getTime());
    }

    @Override
    public java.util.Date convertFromMillis(long systemTimeMillis) {
      return new java.util.Date(systemTimeMillis);
    }
  }

  static final class DateType extends ScalarTypeBaseDate<Date> {

    DateType(JsonConfig.Date mode) {
      super(mode, Date.class, false, Types.DATE);
    }

    @Override
    protected String toIsoFormat(java.util.Date value) {
      return UtilDateParser.format(value);
    }

    @Override
    public long convertToMillis(java.util.Date value) {
      return value.getTime();
    }

    @Override
    public Date convertFromDate(java.sql.Date ts) {
      return new java.util.Date(ts.getTime());
    }

    @Override
    public java.sql.Date convertToDate(Date date) {
      return new java.sql.Date(date.getTime());
    }

    @Override
    public Object toJdbcType(Object value) {
      return BasicTypeConverter.toDate(value);
    }

    @Override
    public java.util.Date toBeanType(Object value) {
      return BasicTypeConverter.toUtilDate(value);
    }
  }
}
