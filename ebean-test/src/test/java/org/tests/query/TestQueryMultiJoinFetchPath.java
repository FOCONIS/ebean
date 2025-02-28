package org.tests.query;

import io.ebean.test.LoggedSql;
import io.ebean.xtest.BaseTestCase;
import io.ebean.DB;
import io.ebean.Query;
import org.junit.jupiter.api.Test;
import org.tests.model.join.*;
import org.tests.model.join.initfields.Order;
import org.tests.model.join.initfields.OrderDetail;
import org.tests.model.join.initfields.OrderInvoice;
import org.tests.model.join.initfields.OrderItem;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestQueryMultiJoinFetchPath extends BaseTestCase {

  @Test
  void test() {
    HCustomer c1 = new HCustomer("c1", "c1");
    DB.save(c1);

    HCustomer c2 = new HCustomer("c2", "c2");
    DB.save(c2);

    HCustomer c3 = new HCustomer("c3", "c3");
    DB.save(c3);

    HAccount a1 = new BankAccount();
    a1.setAccountNumber("a1");
    a1.setOwner(c1);
    DB.save(a1);

    CustomerAccess ca = new CustomerAccess();
    ca.setAccessor(c3);
    ca.setPrincipal(c1);
    DB.save(ca);

    AccountAccess aa = new AccountAccess();
    aa.setAccessor(c2);
    aa.setAccount(a1);
    DB.save(aa);

    List<Object> ids = DB.find(HAccess.class)
      .where()
      .eq("principal.status", "A")
      .eq("accessor.status", "A")
      .findIds();

    assertThat(ids).hasSize(2);

    Query<HAccess> query = DB.find(HAccess.class)
      .fetch("account","accountNumber")
      .fetch("accessor","name")
      .where()
      .eq("accessor.status", "A")
      .eq("principal.status", "A")
      .idIn(ids)
      .query();

    List<HAccess> accesses = query.findList();

    assertThat(accesses).hasSize(2);
    if (isH2()) {
      assertThat(query.getGeneratedSql()).isEqualTo("select t0.dtype, t0.id, t0.accessor_id, t0.principal_id, t2.dtype, t0.access_account_number, t1.cid, t1.name, t2.dtype, t2.account_number from haccess t0 left join hcustomer t1 on t1.cid = t0.accessor_id left join haccount t2 on t2.account_number = t0.access_account_number and t2.dtype = 'B' left join hcustomer t3 on t3.cid = t0.principal_id where t1.status = ? and t3.status = ? and t0.id in (?,?,?,?,?)");
    } else {
      assertThat(query.getGeneratedSql()).contains("select t0.dtype, t0.id, t0.accessor_id, t0.principal_id, t2.dtype, t0.access_account_number, t1.cid, t1.name, t2.dtype, t2.account_number from haccess t0 left join hcustomer t1 on t1.cid = t0.accessor_id left join haccount t2 on t2.account_number = t0.access_account_number and t2.dtype = 'B' left join hcustomer t3 on t3.cid = t0.principal_id where t1.status = ? and t3.status = ? and t0.id ");
    }
  }

  @Test
  public void test_manyNonRoot_RootHasNoMany() {
    Order o = new Order();
    DB.save(o);

    OrderItem p1 = new OrderItem();
    p1.order = o;
    DB.save(p1);

    OrderItem p2 = new OrderItem();
    p2.order = o;
    DB.save(p2);

    OrderDetail d1 = new OrderDetail();
    d1.order = o;
    DB.save(d1);

    OrderDetail d2 = new OrderDetail();
    d2.order = o;
    DB.save(d2);

    OrderInvoice i1 = new OrderInvoice();
    i1.order = o;
    DB.save(i1);

    OrderInvoice i2 = new OrderInvoice();
    i2.order = o;
    DB.save(i2);

    // This first query behaves as expected: a main query and its secondary query.
    LoggedSql.start();
    List<Order> list1 = DB.find(Order.class)
      .fetch("orderItems")
      .fetch("orderDetails")
      .where().gt("id", 0)
      .findList();

    assertEquals(2, list1.get(0).orderItems.size());
    assertEquals(2, list1.get(0).orderDetails.size());

    List<String> sql1 = LoggedSql.stop();
    assertEquals(2, sql1.size());

    // This query does not eager fetch invoices. We get an NPE on orderInvoices. Only the main query is executed.
    LoggedSql.start();
    List<Order> list2 = DB.find(Order.class)
      .fetch("orderItems")
      .fetch("orderInvoices")
      .where().gt("id", 0)
      .findList();

    assertEquals(2, list2.get(0).orderItems.size());
    assertEquals(2, list2.get(0).orderInvoices.size());

    List<String> sql2 = LoggedSql.stop();
    assertEquals(2, sql2.size());
  }
}
