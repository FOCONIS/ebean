package io.ebean.xtest.text;

import io.ebean.CountDistinctOrder;
import io.ebean.xtest.BaseTestCase;
import io.ebean.DB;
import io.ebean.Query;
import io.ebean.test.LoggedSql;
import io.ebean.text.PathProperties;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.model.basic.Customer;
import org.tests.model.basic.ResetBasicData;
import org.tests.model.composite.Model;
import org.tests.model.composite.ModelSubEntity;
import org.tests.model.composite.RCustomer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathPropertiesTests extends BaseTestCase {

  private static final Logger log = LoggerFactory.getLogger(PathPropertiesTests.class);

  @Test
  void example_withQueryAndJson() {
    ResetBasicData.reset();

    PathProperties pathProps = PathProperties.parse("id,name,billingAddress(city),shippingAddress(*))");

    Query<Customer> query = DB.find(Customer.class)
      .where().lt("id", 2)
      .query();

    pathProps.apply(query);

    List<Customer> list = query.findList();

    String asJson = DB.json().toJson(list, pathProps);
    log.info("Json: {}", asJson);
  }

  @Test
  void test_withAllPropsQuery() {
    PathProperties root = PathProperties.parse("*,billingAddress(line1)");
    LoggedSql.start();
    Query<Customer> query = DB.find(Customer.class).apply(root);
    query.findList();
    List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(1);
    assertThat(sql.get(0)).contains("select t0.id, t0.status, t0.name, t0.smallnote, t0.anniversary, t0.cretime, t0.updtime, t0.version, t0.shipping_address_id, t1.id, t1.line_1 from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id;");
  }

  @Test
  void test_withConcat() {

    LoggedSql.start();
    Query<Customer> query = DB.find(Customer.class)
      .select("concat(name,billingAddress.line1)");
      //.fetch("billingAddress", "concat(line1, line2, t0.name)");
      //
    query.findSingleAttributeList();
    List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(1);
    assertThat(sql.get(0)).contains("select t0.id, t0.status, t0.name, t0.smallnote, t0.anniversary, t0.cretime, t0.updtime, t0.version, t0.shipping_address_id, t1.id, t1.line_1 from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id;");
  }

  @Test
  void test_withEmbedededId() {
    ModelSubEntity entity1 = new ModelSubEntity();
    DB.save(entity1);
    ModelSubEntity entity2 = new ModelSubEntity();
    DB.save(entity2);
    ModelSubEntity entity3 = new ModelSubEntity();
    DB.save(entity3);

    Model model1 = new Model();
    model1.setFrom(entity1);
    model1.setTo(entity2);
    model1.setDescription("model 1");
    DB.save(model1);

    Model model2 = new Model();
    model2.setFrom(entity3);
    model2.setTo(entity2);
    model2.setDescription("model 2");
    DB.save(model2);

    Query<Model> defaultQuery = DB.find(Model.class);
    PathProperties root = new PathProperties();
    root.addToPath(null,"id.fromId");
    //root.addToPath("id"","fromId"); so sah mein erster Versuch aus
    LoggedSql.start();
    Query<Model> query = defaultQuery.copy();
    query.apply(root).where().isNotNull("id.fromId");
    query.setDistinct(true).setCountDistinct(CountDistinctOrder.COUNT_DESC_ATTR_ASC);
    query.setMaxRows(1);
    List<Object> returnList = query.findSingleAttributeList();
    List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(1);
    assertThat(sql.get(0)).contains("select distinct r1.attribute_, count(*) from (select distinct t0.from_id, t0.to_id, null as attribute_ from model t0 where t0.from_id is not null) r1 group by r1.attribute_ order by count(*) desc, r1.attribute_ limit 1;");
  }

}
