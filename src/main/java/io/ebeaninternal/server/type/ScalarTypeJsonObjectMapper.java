package io.ebeaninternal.server.type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import io.ebean.annotation.MutationDetection;
import io.ebean.config.dbplatform.DbPlatformType;
import io.ebean.text.TextException;
import io.ebeaninternal.server.deploy.meta.DeployBeanProperty;
import io.ebeanservice.docstore.api.mapping.DocPropertyType;

import javax.persistence.PersistenceException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Supports @DbJson properties using Jackson ObjectMapper.
 */
class ScalarTypeJsonObjectMapper {

  /**
   * Create and return the appropriate ScalarType.
   */
  public static ScalarType<?> createTypeFor(boolean postgres, AnnotatedField field, ObjectMapper objectMapper,
      DeployBeanProperty prop, int dbType, DocPropertyType docType) {

    prop.setMutationDetection(MutationDetection.SOURCE);

    String pgType = getPostgresType(postgres, dbType);

    return new GenericObject(objectMapper, field, dbType, pgType, docType);
  }

  private static String getPostgresType(boolean postgres, int dbType) {
    if (postgres) {
      switch (dbType) {
        case DbPlatformType.JSON:
          return PostgresHelper.JSON_TYPE;
        case DbPlatformType.JSONB:
          return PostgresHelper.JSONB_TYPE;
      }
    }
    return null;
  }

  /**
   * No mutation detection on this json property.
   */
  private static class NoMutationDetection extends Base<Object> {

    NoMutationDetection(ObjectMapper objectMapper, AnnotatedField field, int dbType, String pgType, DocPropertyType docType) {
      super(Object.class, objectMapper, field, dbType, pgType, docType);
    }

    @Override
    public boolean isMutable() {
      return false;
    }

    @Override
    public boolean isDirty(Object value) {
      return false;
    }
  }

  /**
   * Supports HASH and SOURCE dirty detection modes.
   */
  private static class GenericObject extends Base<Object> {

    GenericObject(ObjectMapper objectMapper, AnnotatedField field, int dbType, String pgType, DocPropertyType docType) {
      super(Object.class, objectMapper, field, dbType, pgType, docType);
    }

    @Override
    public boolean isJsonMapper() {
      return true;
    }

    @Override
    public Object read(DataReader reader) throws SQLException {
      String json = reader.getString();
      if (json == null || json.isEmpty()) {
        return null;
      }
      // pushJson such that we MD5 and store on EntityBeanIntercept later
      reader.pushJson(json);
      try {
        return objectReader.readValue(json, deserType);
      } catch (IOException e) {
        throw new TextException("Failed to parse JSON [{}] as " + deserType, json, e);
      }
    }

    @Override
    public void bind(DataBind bind, Object value) throws SQLException {
      // popJson as dirty detection already converted to json string
      String rawJson = bind.popJson();
      if (rawJson == null && value != null) {
        rawJson = formatValue(value); // not expected, need to check?
      }
      if (pgType != null) {
        bind.setObject(PostgresHelper.asObject(pgType, rawJson));
      } else {
        if (value == null) {
          // use varchar, otherwise SqlServer/db2 will fail with 'Invalid JDBC data type 5.001.'
          bind.setNull(Types.VARCHAR);
        } else {
          bind.setString(rawJson);
        }
      }
    }
  }

  /**
   * ScalarType that uses Jackson ObjectMapper to marshall/unmarshall to/from JSON
   * and storing them in one of JSON, JSONB, VARCHAR, CLOB or BLOB.
   */
  private static abstract class Base<T> extends ScalarTypeBase<T> {

    protected final ObjectWriter objectWriter;
    protected final ObjectMapper objectReader;
    protected final JavaType deserType;
    protected final String pgType;
    private final DocPropertyType docType;

    /**
     * Construct given the object mapper, property type and DB type for storage.
     */
    public Base(Class<T> cls, ObjectMapper objectMapper, AnnotatedField field, int dbType, String pgType, DocPropertyType docType) {
      super(cls, false, dbType);
      this.pgType = pgType;
      this.docType = docType;
      this.objectReader = objectMapper;

      JavaType javaType = field.getType();
      DeserializationConfig deserConfig = objectMapper.getDeserializationConfig();
      AnnotationIntrospector ai = deserConfig.getAnnotationIntrospector();

      if (ai != null && javaType != null && !javaType.hasRawClass(Object.class)) {
        try {
          this.deserType = ai.refineDeserializationType(deserConfig, field, javaType);
        } catch (JsonMappingException e) {
          throw new RuntimeException(e);
        }
      } else {
        this.deserType = javaType;
      }

      SerializationConfig serConfig = objectMapper.getSerializationConfig();
       ai = deserConfig.getAnnotationIntrospector();

       if (ai != null && javaType != null && !javaType.hasRawClass(Object.class)) {
         try {
           JavaType serType = ai.refineSerializationType(serConfig, field, javaType);
           this.objectWriter = objectMapper.writerFor(serType);
         } catch (JsonMappingException e) {
           throw new RuntimeException(e);
         }
       } else {
         this.objectWriter = objectMapper.writerFor(javaType);
       }
    }

    @Override
    public boolean isMutable() {
      return true;
    }

    @Override
    public T read(DataReader reader) throws SQLException {
      String json = reader.getString();
      if (json == null || json.isEmpty()) {
        return null;
      }
      try {
        return objectReader.readValue(json, deserType);
      } catch (IOException e) {
        throw new TextException("Failed to parse JSON [{}] as " + deserType, json, e);
      }
    }

    @Override
    public void bind(DataBind bind, T value) throws SQLException {
      if (pgType != null) {
        String rawJson = (value == null) ? null : formatValue(value);
        bind.setObject(PostgresHelper.asObject(pgType, rawJson));
      } else {
        if (value == null) {
          bind.setNull(Types.VARCHAR); // use varchar, otherwise SqlServer/db2 will fail with 'Invalid JDBC data type 5.001.'
        } else {
          bind.setString(formatValue(value));
        }
      }
    }

    @Override
    public Object toJdbcType(Object value) {
      // no type conversion supported
      return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T toBeanType(Object value) {
      // no type conversion supported
      return (T) value;
    }

    @Override
    public String formatValue(T value) {
      try {
        return objectWriter.writeValueAsString(value);
      } catch (JsonProcessingException e) {
        throw new PersistenceException("Unable to create JSON", e);
      }
    }

    @Override
    public T parse(String value) {
      try {
        return objectReader.readValue(value, deserType);
      } catch (IOException e) {
        throw new TextException("Failed to parse JSON [{}] as " + deserType, value, e);
      }
    }

    @Override
    public DocPropertyType getDocType() {
      return docType;
    }

    @Override
    public boolean isDateTimeCapable() {
      return false;
    }

    @Override
    public T convertFromMillis(long dateTime) {
      throw new IllegalStateException("Not supported");
    }

    @Override
    public T jsonRead(JsonParser parser) throws IOException {
      return objectReader.readValue(parser, deserType);
    }

    @Override
    public void jsonWrite(JsonGenerator writer, T value) throws IOException {
      objectWriter.writeValue(writer, value);
    }

    @Override
    public T readData(DataInput dataInput) throws IOException {
      if (!dataInput.readBoolean()) {
        return null;
      } else {
        return parse(dataInput.readUTF());
      }
    }

    @Override
    public void writeData(DataOutput dataOutput, T value) throws IOException {
      if (value == null) {
        dataOutput.writeBoolean(false);
      } else {
        ScalarHelp.writeUTF(dataOutput, format(value));
      }
    }
  }
}
