package com.avaje.ebeaninternal.server.grammer;

import static org.junit.Assume.assumeFalse;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebeaninternal.api.SpiQuery;
import com.avaje.tests.model.basic.Customer;
import com.avaje.tests.model.basic.ResetBasicData;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EqlParserTest extends BaseTestCase {

  @Test
  public void where_eq() throws Exception {

    Query<Customer> query = parse("where name eq 'Rob'");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name = ?");
  }

  @Test
  public void where_ieq() throws Exception {

    Query<Customer> query = parse("where name ieq 'Rob'");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where lower(t0.name) =?");
  }

  @Test
  public void where_eq2() throws Exception {

    Query<Customer> query = parse("where name = 'Rob'");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name = ?");
  }

  @Test
  public void where_namedParam() throws Exception {

    Query<Customer> query = parse("where name eq :name");
    query.setParameter("name", "Rob");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name = ?");
  }

  @Test
  public void where_namedParam_startsWith() throws Exception {

    Query<Customer> query = parse("where name startsWith :name");
    query.setParameter("name", "Rob");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name like ?");
  }

  @Test
  public void where_or1() throws Exception {

    Query<Customer> query = parse("where name = 'Rob' or (status = 'NEW' and smallnote is null)");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where (t0.name = ?  or (t0.status = ?  and t0.smallnote is null ) )");
  }

  @Test
  public void where_or2() throws Exception {

    Query<Customer> query = parse("where (name = 'Rob' or status = 'NEW') and smallnote is null");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where ((t0.name = ?  or t0.status = ? )  and t0.smallnote is null )");
  }

  @Test
  public void test_simplifyExpressions() throws Exception {

    Query<Customer> query = parse("where not (name = 'Rob' and status = 'NEW')");
    query.findList();
    assertThat(query.getGeneratedSql()).contains("where not (t0.name = ?  and t0.status = ? )");

    query = parse("where not ((name = 'Rob' and status = 'NEW'))");
    query.findList();
    assertThat(query.getGeneratedSql()).contains("where not (t0.name = ?  and t0.status = ? )");

    query = parse("where not (((name = 'Rob') and (status = 'NEW')))");
    query.findList();
    assertThat(query.getGeneratedSql()).contains("where not (t0.name = ?  and t0.status = ? )");
  }


  @Test
  public void where_in() throws Exception {

    Query<Customer> query = parse("where name in ('Rob','Jim')");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name in (?, ? )");
  }

  @Test
  public void where_in_when_namedParams() throws Exception {

    Query<Customer> query = parse("where name in (:one, :two)");
    query.setParameter("one", "Foo");
    query.setParameter("two", "Bar");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name in (?, ? )");
  }

  @Test
  public void where_in_when_namedParams_withWhitespace() throws Exception {

    Query<Customer> query = parse("where name in (:one,  :two)");
    query.setParameter("one", "Foo");
    query.setParameter("two", "Bar");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name in (?, ? )");
  }

  @Test
  public void where_in_when_namedParams_withNoWhitespace() throws Exception {

    Query<Customer> query = parse("where name in (:one,:two)");
    query.setParameter("one", "Foo");
    query.setParameter("two", "Bar");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name in (?, ? )");
  }

  @Test
  public void where_in_when_namedParamAsList() throws Exception {

    Query<Customer> query = parse("where name in (:names)");
    query.setParameter("names", Arrays.asList("Baz","Maz","Jim"));
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name in (?, ?, ? )");
  }

  @Test
  public void where_between() throws Exception {

    Query<Customer> query = parse("where name between 'As' and 'B'");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name between  ? and ? ");
  }

  @Test
  public void where_between_withNamedParams() throws Exception {

    Query<Customer> query = parse("where name between :one and :two");
    query.setParameter("one", "a");
    query.setParameter("two", "b");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where t0.name between  ? and ? ");
  }

  @Test
  public void where_betweenProperty() throws Exception {

    Query<Customer> query = parse("where 'x' between name and smallnote");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where  ?  between t0.name and t0.smallnote");
  }

  @Test
  public void where_betweenProperty_withNamed() throws Exception {

    Query<Customer> query = parse("where :some between name and smallnote");
    query.setParameter("some", "A");
    query.findList();

    assertThat(query.getGeneratedSql()).contains("where  ?  between t0.name and t0.smallnote");
  }

  @Test
  public void fetch_basic() throws Exception {

    Query<Customer> query = parse("fetch billingAddress");
    query.findList();

    assertThat(query.getGeneratedSql()).contains(", t1.id c9");
  }

  @Test
  public void fetch_withProperty() throws Exception {

    Query<Customer> query = parse("fetch billingAddress (city)");
    query.findList();

    assertThat(query.getGeneratedSql()).contains(", t1.id c9, t1.city");
  }

  @Test
  public void fetch_withProperty_noWhitespace() throws Exception {

    Query<Customer> query = parse("fetch billingAddress(city)");
    query.findList();

    assertThat(query.getGeneratedSql()).contains(", t1.id c9, t1.city");
  }

  @Test
  public void fetch_basic_multiple() throws Exception {

    Query<Customer> query = parse("fetch billingAddress fetch shippingAddress");
    query.findList();

    assertThat(query.getGeneratedSql()).contains(", t1.city ");
    assertThat(query.getGeneratedSql()).contains(", t2.city ");
  }

  @Test
  public void fetch_basic_multiple_withProperties() throws Exception {

    Query<Customer> query = parse("fetch billingAddress (city) fetch shippingAddress (city)");
    query.findList();

    assertThat(query.getGeneratedSql()).contains(", t1.id c8, t1.city c9, t2.id c10, t2.city c11");
  }

  @Test
  public void fetch_lazy() throws Exception {

    Query<Customer> query = parse("fetch lazy billingAddress");
    query.findList();

    assertThat(query.getGeneratedSql()).doesNotContain(", t1.city ");
  }

  @Test
  public void fetch_lazy50() throws Exception {

    Query<Customer> query = parse("fetch lazy(50) billingAddress");
    query.findList();

    assertThat(query.getGeneratedSql()).doesNotContain(", t1.city ");
  }

  @Test
  public void fetch_query50() throws Exception {

    ResetBasicData.reset();
    Query<Customer> query = parse("fetch query(50) billingAddress");
    query.findList();

    assertThat(query.getGeneratedSql()).doesNotContain(", t1.city ");
  }

  @Test
  public void fetch_query50_asHint() throws Exception {

    ResetBasicData.reset();
    Query<Customer> query = parse("fetch billingAddress (+query(50),city)");
    query.findList();

    assertThat(query.getGeneratedSql()).doesNotContain(", t1.city ");
  }

  @Test
  public void fetch_lazy50_asHint() throws Exception {

    ResetBasicData.reset();
    Query<Customer> query = parse("fetch billingAddress (+lazy(50),city)");
    List<Customer> list = query.findList();

    assertThat(query.getGeneratedSql()).doesNotContain(", t1.city ");

    Customer customer = list.get(0);
    customer.getBillingAddress().getCity();
  }

  @Test
  public void select() throws Exception {

    ResetBasicData.reset();

    Query<Customer> query = parse("select (name)");
    query.findList();
    assertThat(query.getGeneratedSql()).contains("select t0.id c0, t0.name c1 from o_customer t0");
  }

  @Test
  public void selectDistinct() throws Exception {

    ResetBasicData.reset();

    Query<Customer> query = parse("select distinct (name)");
    query.findList();
    assertThat(query.getGeneratedSql()).contains("select distinct t0.name c0 from o_customer t0");
  }


  @Test
  public void limit() throws Exception {

    ResetBasicData.reset();

    Query<Customer> query = parse("limit 10");
    query.findList();
    if (isH2()) {
      assertThat(query.getGeneratedSql()).contains(" limit 10");
    }
  }

  @Test
  public void limitOffset() throws Exception {

    ResetBasicData.reset();

    Query<Customer> query = parse("limit 10 offset 5");
    query.findList();
    if (isH2()) {
      assertThat(query.getGeneratedSql()).contains(" limit 10 offset 5");
    }
  }

  @Test
  public void orderBy() throws Exception {

    ResetBasicData.reset();

    Query<Customer> query = parse("order by id");
    query.findList();
    if (isH2()) {
      assertThat(query.getGeneratedSql()).contains("from o_customer t0 order by t0.id");
    }
  }

  @Test
  public void orderBy_desc() throws Exception {

    ResetBasicData.reset();

    Query<Customer> query = parse("order by id desc");
    query.findList();
    if (isH2()) {
      assertThat(query.getGeneratedSql()).contains("from o_customer t0 order by t0.id desc");
    }
  }

  @Test
  public void orderBy_nullsLast() throws Exception {

    assumeFalse("Skipping test because nulls not yet supported for MS SQL Server.", isMsSqlServer());
    
    ResetBasicData.reset();

    Query<Customer> query = parse("order by id desc nulls last");
    query.findList();
    if (isH2()) {
      assertThat(query.getGeneratedSql()).contains(" from o_customer t0 order by t0.id desc nulls last");
    }
  }

  @Test
  public void orderBy_nullsFirst() throws Exception {

    assumeFalse("Skipping test because nulls not yet supported for MS SQL Server.", isMsSqlServer());
    
    ResetBasicData.reset();

    Query<Customer> query = parse("order by id nulls first");
    query.findList();
    if (isH2()) {
      assertThat(query.getGeneratedSql()).contains(" from o_customer t0 order by t0.id nulls first");
    }
  }

  @Test
  public void orderBy_multiple() throws Exception {
    
    assumeFalse("Skipping test because nulls not yet supported for MS SQL Server.", isMsSqlServer());

    ResetBasicData.reset();

    Query<Customer> query = parse("order by billingAddress.city desc nulls last, name, id desc nulls last");
    query.findList();
    if (isH2()) {
      assertThat(query.getGeneratedSql()).contains(" order by t1.city desc nulls last, t0.name, t0.id desc nulls last");
    }
  }

  private Query<Customer> parse(String raw) {

    Query<Customer> query = Ebean.find(Customer.class);
    EqlParser.parse(raw, (SpiQuery)query);
    return query;
  }

}