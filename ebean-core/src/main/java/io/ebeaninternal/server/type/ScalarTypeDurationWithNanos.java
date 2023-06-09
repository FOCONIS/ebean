package io.ebeaninternal.server.type;

import io.ebean.core.type.BasicTypeConverter;
import io.ebean.core.type.DataBinder;
import io.ebean.core.type.DataReader;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;

/**
 * ScalarType for java.time.Duration (with Nanos precision).
 * <p>
 * Stored in the DB as DECIMAL value.
 * </p>
 */
final class ScalarTypeDurationWithNanos extends ScalarTypeDuration {

  ScalarTypeDurationWithNanos() {
    super(Types.DECIMAL);
  }

  @Override
  public BigDecimal bind(DataBinder binder, Duration value) throws SQLException {
    if (value == null) {
      binder.setNull(Types.DECIMAL);
      return null;
    } else {
      BigDecimal rawValue = convertToBigDecimal(value);
      binder.setBigDecimal(rawValue);
      return rawValue;
    }
  }

  @Override
  public Duration read(DataReader reader) throws SQLException {
    return convertFromBigDecimal(reader.getBigDecimal());
  }

  @Override
  public Object toJdbcType(Object value) {
    if (value instanceof BigDecimal) return value;
    return convertToBigDecimal((Duration) value);
  }

  @Override
  public Duration toBeanType(Object value) {
    if (value instanceof Duration) return (Duration) value;
    if (value == null) return null;
    return convertFromBigDecimal(BasicTypeConverter.toBigDecimal(value));
  }

}
