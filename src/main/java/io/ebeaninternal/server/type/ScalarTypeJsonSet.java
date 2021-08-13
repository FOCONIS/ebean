package io.ebeaninternal.server.type;

import io.ebean.config.dbplatform.DbPlatformType;
import io.ebean.text.TextException;
import io.ebean.text.json.EJson;
import io.ebeanservice.docstore.api.mapping.DocPropertyType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import javax.persistence.PersistenceException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Types for mapping List in JSON format to DB types VARCHAR, JSON and JSONB.
 */
public class ScalarTypeJsonSet {

  /**
   * Return the appropriate ScalarType for the requested dbType and Postgres.
   */
  public static ScalarType<?> typeFor(boolean postgres, int dbType, DocPropertyType docPropertyType) {
    if (postgres) {
      switch (dbType) {
        case DbPlatformType.JSONB:
          return new ScalarTypeJsonSet.JsonB(docPropertyType, true);
        case DbPlatformType.JSON:
          return new ScalarTypeJsonSet.Json(docPropertyType, true);
      }
    }
    return new ScalarTypeJsonSet.Varchar(docPropertyType, true);
  }

  /**
   * List mapped to DB VARCHAR.
   */
  public static class Varchar extends ScalarTypeJsonSet.Base {
    public Varchar(DocPropertyType docPropertyType, boolean keepSource) {
      super(Types.VARCHAR, docPropertyType, keepSource);
    }
  }

  /**
   * List mapped to Postgres JSON.
   */
  private static class Json extends ScalarTypeJsonSet.PgBase {
    public Json(DocPropertyType docPropertyType, boolean keepSource) {
      super(DbPlatformType.JSON, PostgresHelper.JSON_TYPE, docPropertyType, keepSource);
    }
  }

  /**
   * List mapped to Postgres JSONB.
   */
  private static class JsonB extends ScalarTypeJsonSet.PgBase {
    public JsonB(DocPropertyType docPropertyType, boolean keepSource) {
      super(DbPlatformType.JSONB, PostgresHelper.JSONB_TYPE, docPropertyType, keepSource);
    }
  }

  /**
   * Base class for List handling.
   */
  @SuppressWarnings("rawtypes")
  private abstract static class Base extends ScalarTypeJsonCollection<Set> {

    private boolean keepSource;

    public Base(int dbType, DocPropertyType docPropertyType, boolean keepSource) {
      super(Set.class, dbType, docPropertyType);
      this.keepSource = keepSource;
    }


    @Override
    public boolean isJsonMapper() {
      return keepSource;
    }

    @Override
    public Set read(DataReader reader) throws SQLException {
      String json = reader.getString();
      if (isJsonMapper()) {
        reader.pushJson(json);
      }
      try {
        // parse JSON into modifyAware list
        return EJson.parseSet(json, true);
      } catch (IOException e) {
        throw new TextException("Failed to parse JSON [{}] as Set", json, e);
      }
    }

    @Override
    public final void bind(DataBind binder, Set value) throws SQLException {
      String rawJson = isJsonMapper() ? binder.popJson() : null;
      if (rawJson == null && value != null) {
        rawJson = formatValue(value);
      }
      if (value == null) {
        bindNull(binder);
      } else {
        bindRawJson(binder, rawJson);
      }
    }

    protected void bindNull(DataBind binder) throws SQLException {
      binder.setNull(Types.VARCHAR);
    }

    protected void bindRawJson(DataBind binder, String rawJson) throws SQLException {
      binder.setString(rawJson);
    }

    @Override
    public String formatValue(Set value) {
      if (value.isEmpty()) {
        return "[]";
      }
      try {
        return EJson.write(value);
      } catch (IOException e) {
        throw new PersistenceException("Failed to format List into JSON content", e);
      }
    }

    @Override
    public Set parse(String value) {
      try {
        return convertList(EJson.parseList(value));
      } catch (IOException e) {
        throw new PersistenceException("Failed to parse JSON content as Set: [" + value + "]", e);
      }
    }

    @Override
    public Set jsonRead(JsonParser parser) throws IOException {
      return convertList(EJson.parseList(parser, parser.getCurrentToken()));
    }

    @Override
    public void jsonWrite(JsonGenerator writer, Set value) throws IOException {
      EJson.write(value, writer);
    }

    @SuppressWarnings("unchecked")
    private Set convertList(List list) {
      return new LinkedHashSet(list);
    }
  }

  /**
   * Postgres extension to base List handling.
   */
  private static class PgBase extends ScalarTypeJsonSet.Base {

    final String pgType;

    PgBase(int jdbcType, String pgType, DocPropertyType docPropertyType, boolean keepSource) {
      super(jdbcType, docPropertyType, keepSource);
      this.pgType = pgType;
    }

    @Override
    protected void bindRawJson(DataBind binder, String rawJson) throws SQLException {
      binder.setObject(PostgresHelper.asObject(pgType, rawJson));
    }
  }

}
