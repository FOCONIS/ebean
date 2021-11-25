package io.ebeaninternal.server.type;

import io.ebean.config.JsonConfig;
import io.ebean.core.type.DataBinder;
import io.ebean.core.type.DataReader;
import io.ebeaninternal.server.core.BasicTypeConverter;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;

/**
 * ScalarType for java.sql.Timestamp.
 */
final class ScalarTypeTimestamp extends ScalarTypeBaseDateTime<Timestamp> {

  ScalarTypeTimestamp(JsonConfig.DateTime mode) {
    super(mode, Timestamp.class, true, Types.TIMESTAMP, false);
  }

  @Override
  protected String toJsonNanos(Timestamp value) {
    return toJsonNanos(value.getTime()/1000, value.getNanos());
  }

  @Override
  protected String toJsonISO8601(Timestamp value) {
    return value.toInstant().toString();
  }

  @Override
  public long convertToMillis(Timestamp value) {
    return value.getTime();
  }

  @Override
  public Timestamp convertFromMillis(long systemTimeMillis) {
    return new Timestamp(systemTimeMillis);
  }

  @Override
  public Timestamp convertFromTimestamp(Timestamp ts) {
    return ts;
  }

  @Override
  public Timestamp convertFromInstant(Instant ts) {
    return Timestamp.from(ts);
  }

  @Override
  public Timestamp convertToTimestamp(Timestamp t) {
    return t;
  }

  @Override
  public void bind(DataBinder binder, Timestamp value) throws SQLException {
    if (value == null) {
      binder.setNull(Types.TIMESTAMP);
    } else {
      binder.setTimestamp(value, isLocal);
    }
  }

  @Override
  public Timestamp read(DataReader reader) throws SQLException {
    return reader.getTimestamp(isLocal);
  }

  @Override
  public Object toJdbcType(Object value) {
    return BasicTypeConverter.toTimestamp(value);
  }

  @Override
  public Timestamp toBeanType(Object value) {
    return BasicTypeConverter.toTimestamp(value);
  }
}
