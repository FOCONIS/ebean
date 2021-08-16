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
import java.util.List;

/**
 * Types for mapping List in JSON format to DB types VARCHAR, JSON and JSONB.
 */
public class ScalarTypeJsonList {
  /**
   * Return the appropriate ScalarType based requested dbType and if Postgres.
   */
  public static ScalarType<?> typeFor(boolean postgres, int dbType, DocPropertyType docType) {
    if (postgres) {
      switch (dbType) {
        case DbPlatformType.JSONB:
          return new ScalarTypeJsonList.JsonB(docType, true);
        case DbPlatformType.JSON:
          return new ScalarTypeJsonList.Json(docType, true);
      }
    }
    return new ScalarTypeJsonList.Varchar(docType, true);
  }

  /**
   * List mapped to DB VARCHAR.
   */
  public static class Varchar extends ScalarTypeJsonList.Base {
    public Varchar(DocPropertyType docType, boolean keepSource) {
      super(Types.VARCHAR, docType, keepSource);
    }
  }

  /**
   * List mapped to Postgres JSON.
   */
  private static class Json extends ScalarTypeJsonList.PgBase {
    public Json(DocPropertyType docType, boolean keepSource) {
      super(DbPlatformType.JSON, PostgresHelper.JSON_TYPE, docType, keepSource);
    }
  }

  /**
   * List mapped to Postgres JSONB.
   */
  private static class JsonB extends ScalarTypeJsonList.PgBase {
    public JsonB(DocPropertyType docType, boolean keepSource) {
      super(DbPlatformType.JSONB, PostgresHelper.JSONB_TYPE, docType, keepSource);
    }
  }

  /**
   * Base class for List handling.
   */
  @SuppressWarnings("rawtypes")
  private abstract static class Base extends ScalarTypeJsonCollection<List> {
    private final boolean keepSource;


    public Base(int dbType, DocPropertyType docType, boolean keepSource) {
      super(List.class, dbType, docType);
      this.keepSource = keepSource;
    }

    @Override
    public boolean isJsonMapper() {
      return keepSource;
    }

    @Override
    public List read(DataReader reader) throws SQLException {
      String json = reader.getString();
      if (isJsonMapper()) {
        reader.pushJson(json);
      }
      try {
        // parse JSON into modifyAware list
        return EJson.parseList(json, true);
      } catch (IOException e) {
        throw new TextException("Failed to parse JSON [{}] as List", json, e);
      }
    }

    @Override
    public final void bind(DataBind binder, List value) throws SQLException {
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
    public String formatValue(List value) {
      if (value == null || value.isEmpty()) {
        return "[]";
      }
      try {
        return EJson.write(value);
      } catch (IOException e) {
        throw new PersistenceException("Failed to format List into JSON content", e);
      }
    }

    @Override
    public List parse(String value) {
      try {
        return EJson.parseList(value, false);
      } catch (IOException e) {
        throw new TextException("Failed to parse JSON [{}] as List", value, e);
      }
    }

    @Override
    public List jsonRead(JsonParser parser) throws IOException {
      return EJson.parseList(parser, parser.getCurrentToken());
    }

    @Override
    public void jsonWrite(JsonGenerator writer, List value) throws IOException {
      EJson.write(value, writer);
    }

  }

  /**
   * Postgres extension to base List handling.
   */
  private static class PgBase extends ScalarTypeJsonList.Base {

    final String pgType;

    PgBase(int jdbcType, String pgType, DocPropertyType docType, boolean keepSource) {
      super(jdbcType, docType, keepSource);
      this.pgType = pgType;
    }

    @Override
    protected void bindRawJson(DataBind binder, String rawJson) throws SQLException {
      binder.setObject(PostgresHelper.asObject(pgType, rawJson));
    }
  }

}
