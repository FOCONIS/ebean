package org.tests.query;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.FetchConfig;
import io.ebean.Query;
import org.tests.model.basic.Customer;
import org.tests.model.basic.Order;
import org.tests.model.basic.ResetBasicData;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TestQueryFindEach extends BaseTestCase {

  @Test
  public void test() {

    ResetBasicData.reset();

    EbeanServer server = Ebean.getServer(null);

    Query<Customer> query
      = server.find(Customer.class)
      .setAutoTune(false)
      .fetch("contacts", new FetchConfig().query(2)).where().gt("id", 0).orderBy("id")
      .setMaxRows(2);

    final AtomicInteger counter = new AtomicInteger(0);

    query.findEach(customer -> {
      counter.incrementAndGet();
      customer.getName();
    });

    Assert.assertEquals(2, counter.get());
  }

  @Ignore // run manually
  @Test
  public void findWithGcTest() {
    for (int j = 0; j < 20; j++) {
      for (int i = 0; i < 50; i++) {
        Customer c = new Customer();
        c.setName("Customer #" + i);
        DB.save(c);
        Order o1 = new Order();
        o1.setCustomer(c);
        DB.save(o1);
        Order o2 = new Order();
        o2.setCustomer(c);
        DB.save(o2);
      }
      int customerCount = DB.find(Customer.class).findCount();
      AtomicInteger count = new AtomicInteger(customerCount);

      WeakHashMap<Customer, Integer> customers = new WeakHashMap<>();

      DB.find(Customer.class)
      .fetch("orders")
      .findEach(customer -> {
        customers.put(customer, customer.getId());
        if (count.decrementAndGet() == 0) {
          // Trigger garbage collection on last iteration and check if beans disappear
          // from memory
          try {
            System.gc();
            Thread.sleep(100);
            System.gc();
            Thread.sleep(100);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          customers.size(); // expunge stale entries
          System.out.println("Total instances: " + customerCount + ", instances left in memory: " + customers.size());
        }
      });
    }

  }

  /**
   * Test the behaviour when an exception is thrown inside the findVisit().
   */
  @Test(expected = IllegalStateException.class)
  public void testVisitThrowingException() {

    ResetBasicData.reset();

    EbeanServer server = Ebean.getServer(null);

    Query<Customer> query = server.find(Customer.class).setAutoTune(false)
      .fetch("contacts", new FetchConfig().query(2)).where().gt("id", 0).orderBy("id")
      .setMaxRows(2);

    final AtomicInteger counter = new AtomicInteger(0);

    query.findEach(customer -> {
      counter.incrementAndGet();
      if (counter.intValue() > 0) {
        throw new IllegalStateException("cause a failure");
      }
    });

    Assert.assertFalse("Never get here - exception thrown", true);
  }
}
