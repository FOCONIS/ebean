package org.tests.query.other;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.Query;
import org.tests.model.basic.Customer;
import org.tests.model.basic.ResetBasicData;
import org.avaje.test.model.rawsql.inherit.ChildA;
import org.junit.Test;

import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestQuerySingleAttribute extends BaseTestCase {


  @Test
  public void exampleUsage() {

    ResetBasicData.reset();

    List<String> names =
      Ebean.find(Customer.class)
        .setDistinct(true)
        .select("name")
        .where().eq("status", Customer.Status.NEW)
        .orderBy().asc("name")
        .setMaxRows(100)
        .findSingleAttributeList();

    assertThat(names).isNotNull();
  }

  @Test
  public void exampleUsage_otherType() {

    ResetBasicData.reset();

    List<Date> dates =
      Ebean.find(Customer.class)
        .setDistinct(true)
        .select("anniversary")
        .where().isNotNull("anniversary")
        .orderBy().asc("anniversary")
        .findSingleAttributeList();

    assertThat(dates).isNotNull();
  }

  @Test
  public void withOrderBy() {

    Query<Customer> query =
      Ebean.find(Customer.class)
        .setDistinct(true)
        .select("name")
        .where().eq("status", Customer.Status.NEW)
        .orderBy().asc("name")
        .setMaxRows(100);

    query.findSingleAttributeList();
    if (isSqlServer()) {
      assertThat(sqlOf(query)).contains("select distinct top 100 t0.name from o_customer t0 where t0.status = ?  order by t0.name");
    } else {
      assertThat(sqlOf(query)).contains("select distinct t0.name from o_customer t0 where t0.status = ?  order by t0.name limit 100");
    }
  }

  @Test
  public void basic() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class).select("name");

    List<String> names = query.findSingleAttributeList();//String.class);

    assertThat(sqlOf(query)).contains("select t0.name from o_customer t0");
    assertThat(names).isNotNull();
  }

  @Test
  public void distinctAndWhere() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .select("name")
      .where().eq("status", Customer.Status.NEW)
      .query();

    List<String> names = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t0.name from o_customer t0 where t0.status = ? ");
    assertThat(names).isNotNull();
  }

  @Test
  public void distinctWhereWithJoin() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .select("name")
      .where().eq("status", Customer.Status.NEW)
      .istartsWith("billingAddress.city", "auck")
      .query();

    List<String> names = query.findSingleAttributeList();

    assertThat(sqlOf(query)).contains("select distinct t0.name from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id  where t0.status = ?  and lower(t1.city) like ?");
    assertThat(names).isNotNull();
  }


  @Test
  public void queryPlan_expect_differentPlans() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class).select("name");
    query.findSingleAttributeList();
    assertThat(sqlOf(query)).contains("select t0.name from o_customer t0");

    Query<Customer> query2 = Ebean.find(Customer.class).select("name");
    query2.findList();
    assertThat(sqlOf(query2, 1)).contains("select t0.id, t0.name from o_customer t0");
  }


  @Test
  public void distinctWithElPath() {

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .fetchProperties("billingAddress.city")
      .setMaxRows(100);

    List<String> cities = query.findSingleAttributeList();
    if (isSqlServer()) {
      assertThat(sqlOf(query)).contains("select distinct top 100 t1.city from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id");
    } else {
      assertThat(sqlOf(query)).contains("select distinct t1.city from o_customer t0 left join o_address t1 on t1.id = t0.billing_address_id");
    }
    assertThat(cities).isNotNull();
  }

  @Test
  public void testDistinctWithElPathOnId(){
    Query<Customer> query = Ebean.find(Customer.class)
      .setDistinct(true)
      .fetchProperties("id")
      .setMaxRows(100);

    List<String> ids = query.findSingleAttributeList();
    if (isSqlServer()) {
      assertThat(sqlOf(query)).contains("select distinct top 100 t0.id from o_customer t0");
    } else {
      assertThat(sqlOf(query)).contains("select distinct t0.id from o_customer t0 limit 100");
    }
    assertThat(ids).isNotNull();
  }

  @Test
  public void distinctWithKind() {

    ResetBasicData.reset();

    Query<ChildA> query = Ebean.find(ChildA.class)
      .setDistinct(true)
      .fetchProperties("more")
      .setMaxRows(100);

    List<String> cities = query.findSingleAttributeList();
    if (isSqlServer()) {
      assertThat(sqlOf(query)).contains("select distinct top 100 t0.more from rawinherit_parent t0 where t0.type = 'A'");
    } else {
      assertThat(sqlOf(query)).contains("select distinct t0.more from rawinherit_parent t0 where t0.type = 'A'  limit 100");
    }
    assertThat(cities).isNotNull();
  }

}
