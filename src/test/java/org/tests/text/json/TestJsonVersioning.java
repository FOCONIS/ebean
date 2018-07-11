package org.tests.text.json;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.plugin.BeanType;
import io.ebean.text.json.JsonIOException;
import io.ebean.text.json.JsonReadOptions;
import io.ebean.text.json.JsonVersionMigrationHandler;
import io.ebean.text.json.JsonVersionWriter;
import io.ebean.text.json.JsonWriteOptions;
import io.ebean.text.json.JsonWriter;

import org.tests.inheritance.abstrakt.AbstractBaseBlock;
import org.tests.inheritance.abstrakt.Block;
import org.tests.model.basic.Order;
import org.tests.model.basic.OrderShipment;
import org.tests.model.basic.ResetBasicData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This test case demonstrates, what happens in the lifetime of json models, and how the JSON-migration works.
 *
 * (it is a good idea, to keep a version information from the beginning.)
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class TestJsonVersioning extends BaseTestCase {

  @Before
  public void prepare() {
    ResetBasicData.reset();
    Ebean.getServerCacheManager().getBeanCache(Order.class).clear();
  }
  /**
   * This is a very simple JsonVersionMigrationHander that writes "_v=23" for Order beans,
   * "_v=17" for OrderShipment beans and "_v=1" for all other beans. In practice you will read
   * this version from the model class as static field (similar to SERIAL_VERSION_UID)
   *
   * @author Roland Praml, FOCONIS AG
   *
   */
  private static class TestJsonVersionWriter implements JsonVersionWriter {

    @Override
    public void writeVersion(JsonWriter writer, BeanType<?> beanType) {
      if (beanType.getBeanType().equals(Order.class)) {
        writer.writeNumberField("_v", 23);
      } else if (beanType.getBeanType().equals(OrderShipment.class)) {
        writer.writeNumberField("_v", 17);
      } else if (beanType.getBeanType().equals(Block.class)) {
        writer.writeNumberField("_v", 3);
      } else {
        writer.writeNumberField("_v", 1);
      }
    }
  }

  /**
   * This is a sample to demonstrate, how JSON migration can work.
   *
   * Writing JSON migration can get complex, so it is always better to spend enough time when designing
   * your dataModel.
   *
   * @author Roland Praml, FOCONIS AG
   *
   */
  private static class MigHandler implements JsonVersionMigrationHandler {

    @Override
    public ObjectNode migrateRoot(ObjectNode node, ObjectMapper mapper, BeanType<?> rootBeanType) throws IOException {
      int version = node.get("_v") == null ? 1 : node.get("_v").asInt();


      if (rootBeanType.getBeanType() == AbstractBaseBlock.class && version == 1) {
        Map<String, JsonNode> children = new LinkedHashMap<>();
        node.fields().forEachRemaining(e -> children.put(e.getKey(), e.getValue()));
        node.removeAll();
        // there is no putFirst, so we have to trick here.
        node.put("case_type", "2");
        node.setAll(children);
        version = 2;
      }

      node.put("_v", version);
      return node;
    }

    @Override
    public ObjectNode migrate(ObjectNode node, ObjectMapper mapper, BeanType<?> beanType) throws IOException {
      int version = node.get("_v") == null ? 1 : node.get("_v").asInt();

      if (beanType.getBeanType().equals(Order.class)) {
        // === Order migration path
        if (version == 22) { // in version 22->23 we had a typo in shipments
          node.set("shipments", node.remove("shipmetns"));
          version = 23;
          node.put("_v", version);
        }
        if (version == 23) { // current version
          return node;
        }
      } else if (beanType.getBeanType().equals(OrderShipment.class)) {
        // === OrderShipment migration path
        if (version == 15) { // in version 15->16 we have changed the field name "updatedtime" -> "cretime"
          // and changed the resolution from seconds to millis
          node.put("updtime", node.remove("updatedtime").longValue() * 1000);
          node.put("createdtime", node.get("createdtime").longValue() * 1000);
          version = 16;
          node.put("_v", version);
        }
        if (version == 16) { // in version 16->17 we have changed the field name "createdtime" -> "cretime"
          node.set("cretime",  node.remove("createdtime"));
          version = 17;
          node.put("_v", version);
        }
        if (version == 17) { // current version
          return node;
        }
      } else if (beanType.getBeanType().equals(Block.class)) {
        if (version == 2) { // old. prop was named xxx
          node.put("name",  node.remove("xxx").asText());
          version = 3;
          node.put("_v", version);
        }
        if (version == 3) { // current version
          return node;
        }
      }else {
        // all other beans that have no migration path.
        if (version == 1) { // current version
          return node;
        }
      }

      throw new JsonIOException("No migration path from " + beanType.getName() + "(v=" + version+")");
    }

  }


  @Test
  public void testWriteVersion() throws IOException {

    Order o = Ebean.find(Order.class).setId(1).fetch("shipments").findOne();

    JsonWriteOptions jsonOpts = new JsonWriteOptions();
    jsonOpts.setVersionWrite(new TestJsonVersionWriter());
    String s = Ebean.json().toJson(o, jsonOpts);

    s = s.replaceAll("\\d{5,}", "XXXX"); // replaces all timestamps
    assertThat(s).isEqualTo("{\"_v\":23,\"id\":1,\"status\":\"NEW\",\"orderDate\":XXXX,\"shipDate\":null,"
        + "\"customer\":{\"_v\":1,\"id\":1},\"customerName\":\"Rob\","
        + "\"shipments\":[{\"_v\":17,\"id\":1,\"shipTime\":XXXX,\"cretime\":XXXX,\"updtime\":XXXX,\"version\":1}],"
        + "\"cretime\":XXXX,\"updtime\":XXXX,\"totalAmount\":null,\"totalItems\":null}");

  }

  @Test
  public void testMigrateVersion22_15() throws IOException {

    String s = "{\"_v\":22,\"id\":101,\"status\":\"NEW\",\"orderDate\":1519772400000,\"shipDate\":null,"
        + "\"customer\":{\"_v\":1,\"id\":101},\"customerName\":\"Roland\","
        + "\"shipmetns\":[{\"_v\":15,\"id\":101,\"shipTime\":1519772400000,\"createdtime\":1519012300,\"updatedtime\":1519012400,\"version\":1}],"
        + "\"cretime\":1519772400000,\"updtime\":1519772400000,\"totalAmount\":null,\"totalItems\":null}";

    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    Order o = Ebean.json().toBean(Order.class, s, options);
    assertThat(o.getCustomerName()).isEqualTo("Roland");
    assertThat(o.getShipments()).hasSize(1);
    assertThat(o.getShipments().get(0).getCretime().getTime()).isEqualTo(1519012300000L);
    assertThat(o.getShipments().get(0).getUpdtime().getTime()).isEqualTo(1519012400000L);

  }

  @Test
  public void testMigrateVersion23_15() throws IOException {

    String s = "{\"_v\":23,\"id\":101,\"status\":\"NEW\",\"orderDate\":1519772400000,\"shipDate\":null,"
        + "\"customer\":{\"_v\":1,\"id\":101},\"customerName\":\"Roland\","
        + "\"shipments\":[{\"_v\":15,\"id\":101,\"shipTime\":1519772400000,\"createdtime\":1519012300,\"updatedtime\":1519012400,\"version\":1}],"
        + "\"cretime\":1519772400000,\"updtime\":1519772400000,\"totalAmount\":null,\"totalItems\":null}";

    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    Order o = Ebean.json().toBean(Order.class, s, options);
    assertThat(o.getCustomerName()).isEqualTo("Roland");
    assertThat(o.getShipments()).hasSize(1);
    assertThat(o.getShipments().get(0).getCretime().getTime()).isEqualTo(1519012300000L);
    assertThat(o.getShipments().get(0).getUpdtime().getTime()).isEqualTo(1519012400000L);

  }

  @Test
  public void testMigrateVersion23_16() throws IOException {

    String s = "{\"_v\":23,\"id\":101,\"status\":\"NEW\",\"orderDate\":1519772400000,\"shipDate\":null,"
        + "\"customer\":{\"_v\":1,\"id\":101},\"customerName\":\"Roland\","
        + "\"shipments\":[{\"_v\":16,\"id\":101,\"shipTime\":1519772400000,\"createdtime\":1519012300000,\"updtime\":1519012400000,\"version\":1}],"
        + "\"cretime\":1519772400000,\"updtime\":1519772400000,\"totalAmount\":null,\"totalItems\":null}";

    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    Order o = Ebean.json().toBean(Order.class, s, options);
    assertThat(o.getCustomerName()).isEqualTo("Roland");
    assertThat(o.getShipments()).hasSize(1);
    assertThat(o.getShipments().get(0).getCretime().getTime()).isEqualTo(1519012300000L);
    assertThat(o.getShipments().get(0).getUpdtime().getTime()).isEqualTo(1519012400000L);

  }

  @Test
  public void testMigrateVersion23_17() throws IOException {

    String s = "{\"_v\":23,\"id\":101,\"status\":\"NEW\",\"orderDate\":1519772400000,\"shipDate\":null,"
        + "\"customer\":{\"_v\":1,\"id\":101},\"customerName\":\"Roland\","
        + "\"shipments\":[{\"_v\":17,\"id\":101,\"shipTime\":1519772400000,\"cretime\":1519012300000,\"updtime\":1519012400000,\"version\":1}],"
        + "\"cretime\":1519772400000,\"updtime\":1519772400000,\"totalAmount\":null,\"totalItems\":null}";

    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    Order o = Ebean.json().toBean(Order.class, s, options);
    assertThat(o.getCustomerName()).isEqualTo("Roland");
    assertThat(o.getShipments()).hasSize(1);
    assertThat(o.getShipments().get(0).getCretime().getTime()).isEqualTo(1519012300000L);
    assertThat(o.getShipments().get(0).getUpdtime().getTime()).isEqualTo(1519012400000L);

  }

  @Test(expected = JsonIOException.class)
  public void testMigrateVersion23_18() throws IOException {

    String s = "{\"_v\":23,\"id\":101,\"status\":\"NEW\",\"orderDate\":1519772400000,\"shipDate\":null,"
        + "\"customer\":{\"_v\":1,\"id\":101},\"customerName\":\"Roland\","
        + "\"shipments\":[{\"_v\":18,\"id\":101,\"shipTime\":1519772400000,\"cretime\":1519772400000,\"updtime\":1519772400000,\"version\":1}],"
        + "\"cretime\":1519772400000,\"updtime\":1519772400000,\"totalAmount\":null,\"totalItems\":null}";

    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    Ebean.json().toBean(Order.class, s, options);

  }

  @Test
  public void testWriteVersionInherit() throws IOException {

    Block block = new Block();
        block.setId(1);
    block.setName("Test");

    JsonWriteOptions jsonOpts = new JsonWriteOptions();
    jsonOpts.setVersionWrite(new TestJsonVersionWriter());
    String s = Ebean.json().toJson(block, jsonOpts);

    assertThat(s).isEqualTo("{\"_v\":3,\"case_type\":\"2\",\"id\":1,\"name\":\"Test\"}");

  }

  @Test
  public void testReadVersionReadConcrete() throws IOException {

    String s = "{\"_v\":3,\"case_type\":\"2\",\"id\":1,\"name\":\"Test\"}";
    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    Block block = Ebean.json().toBean(Block.class, s, options);


    assertThat(block.getName()).isEqualTo("Test");

  }

  @Test
  public void testReadVersionReadAbstract() throws IOException {

    String s = "{\"_v\":3,\"case_type\":\"2\",\"id\":1,\"name\":\"Test\"}";
    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    AbstractBaseBlock block = Ebean.json().toBean(AbstractBaseBlock.class, s, options);

    assertThat(block).isInstanceOf(Block.class);
    assertThat(block.getName()).isEqualTo("Test");

  }

  @Test
  public void testReadVersionReadAbstractOldVersion() throws IOException {

    String s = "{\"_v\":2,\"case_type\":\"2\",\"id\":1,\"xxx\":\"Test\"}";
    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    AbstractBaseBlock block = Ebean.json().toBean(AbstractBaseBlock.class, s, options);

    assertThat(block).isInstanceOf(Block.class);
    assertThat(block.getName()).isEqualTo("Test");

  }

  @Test
  public void testReadVersionReadAbstractOlderVersion() throws IOException {

    String s = "{\"_v\":1,\"id\":1,\"xxx\":\"Test\"}";
    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    AbstractBaseBlock block = Ebean.json().toBean(AbstractBaseBlock.class, s, options);

    assertThat(block).isInstanceOf(Block.class);
    assertThat(block.getName()).isEqualTo("Test");

  }

  @Test
  public void testReadVersionReadAbstractNoVersion() throws IOException {

    String s = "{\"id\":1,\"xxx\":\"Test\"}";
    JsonReadOptions options = new JsonReadOptions();
    options.setVersionMigrationHandler(new MigHandler());
    AbstractBaseBlock block = Ebean.json().toBean(AbstractBaseBlock.class, s, options);

    assertThat(block).isInstanceOf(Block.class);
    assertThat(block.getName()).isEqualTo("Test");

  }
}
