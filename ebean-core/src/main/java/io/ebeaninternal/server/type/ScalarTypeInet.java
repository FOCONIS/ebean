package io.ebeaninternal.server.type;

import io.ebean.config.dbplatform.ExtraDbTypes;
import io.ebean.core.type.DataBinder;
import io.ebean.core.type.PostgresHelper;
import io.ebean.core.type.ScalarTypeBaseVarchar;
import io.ebean.types.Inet;

import java.sql.SQLException;
import java.sql.Types;

/**
 * ScalarType for Inet to Varchar or Postgres INET.
 */
abstract class ScalarTypeInet extends ScalarTypeBaseVarchar<Inet> {

  ScalarTypeInet(int jdbcType) {
    super(Inet.class, false, jdbcType);
  }

  @Override
  public abstract String bind(DataBinder binder, Inet value) throws SQLException;

  @Override
  public Inet convertFromDbString(String dbValue) {
    return parse(dbValue);
  }

  @Override
  public String convertToDbString(Inet beanValue) {
    return formatValue(beanValue);
  }

  @Override
  public String formatValue(Inet value) {
    return value.getAddress();
  }

  @Override
  public Inet parse(String value) {
    return new Inet(value);
  }

  /**
   * Inet to Varchar.
   */
  public static final class Varchar extends ScalarTypeInet {

    Varchar() {
      super(Types.VARCHAR);
    }

    @Override
    public String bind(DataBinder binder, Inet value) throws SQLException {
      if (value == null) {
        binder.setNull(Types.VARCHAR);
        return null;
      } else {
        String rawValue = convertToDbString(value);
        binder.setString(rawValue);
        return rawValue;
      }
    }
  }

  /**
   * Inet to Postgres INET.
   */
  public static final class Postgres extends ScalarTypeInet {

    Postgres() {
      super(ExtraDbTypes.INET);
    }

    @Override
    public String bind(DataBinder binder, Inet value) throws SQLException {
      if (value == null) {
        binder.setNull(Types.OTHER);
        return null;
      } else {
        String strValue = convertToDbString(value);
        binder.setObject(PostgresHelper.asInet(strValue));
        return strValue;
      }
    }
  }
}
