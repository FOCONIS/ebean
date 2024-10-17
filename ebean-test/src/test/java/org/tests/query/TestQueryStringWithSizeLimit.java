package org.tests.query;

import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.Query;
import io.ebean.test.LoggedSql;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.Customer;
import org.tests.model.basic.ResetBasicData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class TestQueryStringWithSizeLimit extends BaseTestCase {

  private String SEARCH_VALUE = "a".repeat(41);
  private String DB2_SEARCH_VALUE = "€".repeat(14);

  @Test
  public void testEq() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name,status").where().eq("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where 1=0");

    query = DB.find(Customer.class).select("id,name,status").where().eq("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name = ?");

    query = DB.find(Customer.class).select("id,name,status").where().not().eq("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (1=0)");

  }

  @Test
  public void testNe() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().ne("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where 1=0");

    query = DB.find(Customer.class).select("id,name, status").where().ne("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name <> ?");

    query = DB.find(Customer.class).select("id,name, status").where().not().ne("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (1=0)");

  }

  @Test
  public void testStartsWith() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().startsWith("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape'|'"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().startsWith("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape'|'");

    query = DB.find(Customer.class).select("id,name, status").where().not().startsWith("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (t0.name like ? escape'|')"); // FIXME: not (1=0)

  }

  @Test
  public void testIStartsWith() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().istartsWith("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape'|'"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().istartsWith("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape'|'");

    query = DB.find(Customer.class).select("id,name, status").where().not().istartsWith("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (lower(t0.name) like ? escape'|')"); // FIXME: not (1=0)

  }

  @Test
  public void testEndsWith() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().endsWith("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape'|'"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().endsWith("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape'|'");

    query = DB.find(Customer.class).select("id,name, status").where().not().endsWith("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (t0.name like ? escape'|')"); // FIXME: not (1=0)

  }

  @Test
  public void testIEndsWith() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().iendsWith("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape'|'"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().iendsWith("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape'|'");

    query = DB.find(Customer.class).select("id,name, status").where().not().iendsWith("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (lower(t0.name) like ? escape'|')"); // FIXME: not (1=0)

  }

  @Test
  public void testContains() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().contains("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape'|'"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().contains("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape'|'");

    query = DB.find(Customer.class).select("id,name, status").where().not().contains("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (t0.name like ? escape'|')"); // FIXME: not (1=0)

  }

  @Test
  public void testIContains() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().icontains("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape'|'"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().icontains("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape'|'");

    query = DB.find(Customer.class).select("id,name, status").where().not().icontains("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (lower(t0.name) like ? escape'|')"); // FIXME: not (1=0)

  }

  @Test
  public void testLike() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().like("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape''"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().like("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name like ? escape''");

    query = DB.find(Customer.class).select("id,name, status").where().not().like("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (t0.name like ? escape'')"); // FIXME: not (1=0)

  }

  @Test
  public void testILike() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().ilike("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape''"); //FIXME: 1 = 0

    query = DB.find(Customer.class).select("id,name, status").where().ilike("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where lower(t0.name) like ? escape''");

    query = DB.find(Customer.class).select("id,name, status").where().not().ilike("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (lower(t0.name) like ? escape'')"); // FIXME: not (1=0)

  }

  @Test
  public void testIn() {

    ResetBasicData.reset();

    Query<Customer> query = DB.find(Customer.class).select("id,name, status").where().in("name", SEARCH_VALUE).query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where 1=0");

    query = DB.find(Customer.class).select("id,name, status").where().in("name", "Rob").query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where t0.name in (?)");

    query = DB.find(Customer.class).select("id,name, status").where().not().in("name", SEARCH_VALUE).endNot().query();
    query.findList();
    assertThat(query.getGeneratedSql()).isEqualTo("select t0.id, t0.name, t0.status from o_customer t0 where not (1=0)");

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
