package org.tests.basic;

import io.ebean.CountDistinctOrder;
import io.ebean.DB;
import io.ebean.ExpressionList;
import io.ebean.Query;
import io.ebean.annotation.Platform;
import io.ebean.test.LoggedSql;
import io.ebean.xtest.BaseTestCase;
import io.ebean.xtest.ForPlatform;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.Customer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestQueryWithSubquery extends BaseTestCase {

  @Test
  @ForPlatform({Platform.DB2})
  public void testSubQueryWithCountDistinctDb2() {

    Query<Customer> query = DB.find(Customer.class);
    query.select("cast(contacts.firstName as varchar(3))::String");

    query.setCountDistinct(CountDistinctOrder.NO_ORDERING);

    ExpressionList<?> el = query.where();
    el = el.or();
    el = el.and();
    el.isNotNull("contacts.firstName");
    el = el.endAnd();
    el = el.endOr();

    LoggedSql.start();
    query.findSingleAttributeList();
    final List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(0);
  }

  @Test
  @ForPlatform({Platform.H2})
  public void testSubQueryWithCountDistinctH2() {

    Query<Customer> query = DB.find(Customer.class);
    query.select("convert(contacts.firstName,varchar(3))");

    // simple property geht, wie z.B:
    // query.select("convert(name,varchar(3))");
    // --> select r1.attribute_, count(*) from (select convert(t0.name,varchar(3)) as attribute_ from o_customer t0) r1 group by r1.attribute_;

    // mit prop.prop geht es aber nicht, z.B.
    // query.select("convert(billingAddress.line1,varchar(3))");

    query.setCountDistinct(CountDistinctOrder.NO_ORDERING);

    // Den Block haben wir in AbstractEbeanGridRowDataProvider dabei, aber ist nicht unbedingt nötig, den Fehler zu replizieren
//    ExpressionList<?> el = query.where();
//    el = el.or();
//    el = el.and();
//    el.isNotNull("contacts.firstName");
//    el = el.endAnd();
//    el = el.endOr();

    LoggedSql.start();
    query.findSingleAttributeList();
    final List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(0);
  }

}
