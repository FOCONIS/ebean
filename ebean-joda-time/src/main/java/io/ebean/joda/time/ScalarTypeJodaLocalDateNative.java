package io.ebean.joda.time;

import io.ebean.config.JsonConfig;
import io.ebean.core.type.DataBinder;
import io.ebean.core.type.DataReader;
import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.sql.Types;

/**
 * ScalarType for Joda LocalDate. This maps to a LocalDate. Not all drivers/platforms may support this.
 */
final class ScalarTypeJodaLocalDateNative extends ScalarTypeJodaLocalDate {

  ScalarTypeJodaLocalDateNative(JsonConfig.Date mode) {
    super(mode);
  }

  @Override
  public java.time.LocalDate bind(DataBinder binder, LocalDate value) throws SQLException {
    if (value == null) {
      binder.setNull(Types.DATE);
      return null;
    } else {
      java.time.LocalDate ld = java.time.LocalDate.of(value.getYear(), value.getMonthOfYear(), value.getDayOfMonth());
      binder.setObject(ld);
      return ld;
    }
  }

  @Override
  public LocalDate read(DataReader reader) throws SQLException {
    java.time.LocalDate jtDate = reader.getObject(java.time.LocalDate.class);
    return jtDate == null ? null : new org.joda.time.LocalDate(jtDate.getYear(), jtDate.getMonthValue(), jtDate.getDayOfMonth());
  }
}
