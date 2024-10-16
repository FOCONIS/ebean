package org.tests.query;

import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.Query;
import io.ebean.test.LoggedSql;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.Customer;
import org.tests.model.basic.ResetBasicData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestQueryStringWithSizeLimit extends BaseTestCase {

  private String SEARCH_VALUE = "a".repeat(41);
  private String DB2_SEARCH_VALUE = "€".repeat(14);

  @Test
  public void testEq() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name,status").where().eq("name", SEARCH_VALUE).query();
    List<Customer> list = query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where 1=0");

    query = DB.find(Customer.class).select("id,name,status").where().eq("name", "Rob").query();
    list = query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name = ?");

    //assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testNe() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().ne("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    //assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }


  @Test
  public void testIn() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().in("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(5);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testNotIn() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().notIn("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testStartsWith() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().startsWith("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testIStartsWith() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().istartsWith("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testEndsWith() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().endsWith("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testIEndsWith() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().iendsWith("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testContains() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().contains("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testIContains() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().icontains("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testLike() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().like("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testILike() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().ilike("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testGt() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().gt("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    //assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testGe() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().ge("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    //assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testLt() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().lt("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    //assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testLe() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().le("name", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    //assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  @Test
  public void testBetween() {

    ResetBasicData.reset();

    LoggedSql.start();
    List<Customer> list = DB.find(Customer.class).select("id,name, status").where().between("name", "a", DB2_SEARCH_VALUE).findList();
    List<String> stop = LoggedSql.stop();

    assertThat(list).isEmpty();
    //assertThat(stop).hasSize(1);
    //assertThat(stop.get(0)).contains("--bind(" + SEARCH_VALUE);

  }

  // Tests noch für not(), and(), or(), match?, lgIfPresent?

}
