/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.query;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.Assert;
import org.junit.Test;
import org.tests.model.basic.Country;
import org.tests.model.basic.Customer;
import org.tests.model.basic.OrderShipment;
import org.tests.model.basic.ResetBasicData;


import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Query;
import io.ebean.Transaction;

/**
 * Tests for MariaDb SubQueries.
 *
 * @author Noemi Szemenyei, FOCONIS AG
 *
 */
public class TestMariaDbSubQuery extends BaseTestCase {

  @Test
  public void testUpdateQuery() {

    ResetBasicData.reset();
    try (Transaction txn = DB.beginTransaction()) {
      Country c = DB.find(Country.class).where().eq("code", "NZ").findOne();
      Query<Customer> query = DB.update(Customer.class)
          .set("shippingAddress", null)
          .where()
          .eq("shippingAddress.country", c)
          .query();

      int count = query.update();

      if (isMySql()) {
        assertThat(query.getGeneratedSql()).isEqualTo("update o_customer set shipping_address_id=null  where id in (select * from (select t0.id from o_customer t0 left join o_address t1 on t1.id = t0.shipping_address_id  where t1.country_code = ?) _t)");
      } else {
        assertThat(query.getGeneratedSql()).isEqualTo("update o_customer set shipping_address_id=null  where id in (select t0.id from o_customer t0 left join o_address t1 on t1.id = t0.shipping_address_id  where t1.country_code = ?)");
      }
      Assert.assertEquals(count, 3);

      txn.rollback(); // do not modify test data
    }
  }

  @Test
  public void testUpdateQueryWithSq() {

    ResetBasicData.reset();
    try (Transaction txn = DB.beginTransaction()) {
      Query<Customer> sq = DB.find(Customer.class).select("anniversary").where().eq("name", "Rob").query();
      Query<Customer> query = DB.update(Customer.class)
          .set("name", "Roland")
          .where()
          .in("anniversary", sq)
          .query();

      int count = query.update();

      if (isMySql()) {
        assertThat(query.getGeneratedSql()).isEqualTo("update o_customer set name=? where  (anniversary) in (select * from (select anniversary from o_customer t0 where name = ?) _t)");
      } else {
        assertThat(query.getGeneratedSql()).isEqualTo("update o_customer set name=? where  (anniversary) in (select anniversary from o_customer t0 where name = ?)");
      }
      Assert.assertEquals(count, 1);

      txn.rollback(); // do not modify test data
    }
  }

  @Test
  public void testDeleteQuery() {

    ResetBasicData.reset();
    try (Transaction txn = DB.beginTransaction()) {


      Query<OrderShipment> query = DB.find(OrderShipment.class)
          .where()
          .eq("order.customer.name", "Rob")
          .query();

      int count = query.delete();
      if (isMySql()) {
        assertThat(query.getGeneratedSql()).contains("delete from or_order_ship where id in "
            + "(select * from (select t0.id from or_order_ship t0 "
            + "left join o_order t1 on t1.id = t0.order_id  "
            + "left join o_customer t2 on t2.id = t1.kcustomer_id  where t2.name = ?) _t)");
      } else {
        assertThat(query.getGeneratedSql()).contains("delete from or_order_ship where id in "
            + "(select t0.id from or_order_ship t0 "
            + "left join o_order t1 on t1.id = t0.order_id  "
            + "left join o_customer t2 on t2.id = t1.kcustomer_id  where t2.name = ?)");
      }

      Assert.assertEquals(count, 3);

      txn.rollback(); // do not modify test data
    }
  }

  @Test
  public void testDeleteQueryWithSq() {

    ResetBasicData.reset();
    try (Transaction txn = DB.beginTransaction()) {
      Query<OrderShipment> sq = DB.find(OrderShipment.class).select("version").where().ge("version", 0).query();
      Query<OrderShipment> query = DB.find(OrderShipment.class)
          .where()
          .in("version", sq)
          .query();

      int count = query.delete();

      if (isMySql()) {
        assertThat(query.getGeneratedSql()).isEqualTo("delete t0 from or_order_ship t0 where  (t0.version) in (select * from (select t0.version from or_order_ship t0 where t0.version >= ?) _t)");
      } else if (isH2()){
        assertThat(query.getGeneratedSql()).isEqualTo("delete from or_order_ship t0 where  (t0.version) in (select t0.version from or_order_ship t0 where t0.version >= ?)");
      } else if (isSqlServer()) {
        assertThat(query.getGeneratedSql()).isEqualTo("delete from or_order_ship where  (version) in (select t0.version from or_order_ship t0 where t0.version >= ?)");
      }
      Assert.assertEquals(count, 5);

      txn.rollback(); // do not modify test data
    }
  }

}
