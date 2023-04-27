package org.tests.transaction;

import io.ebean.DB;
import io.ebean.annotation.Transactional;
import io.ebean.annotation.TxType;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.Customer;

public class TestTransactionPerformance extends BaseTestCase {


  private static final int ITERATIONS = 100000;

  /**
   * H2: 207468 Transactions per seconds
   * sqlserver19: 1863 Transactions per seconds
   * mariadb: 252525 Transactions per seconds
   * db2: 6553 Transactions per seconds
   * <p>
   * with lazy transactions: 299401 Transactions per seconds
   */
  @Test
  public void beginTransaction() {
    // warmup
    for (int i = 0; i < 100; i++) {
      getCustomer();
    }

    long time = System.currentTimeMillis();
    for (int i = 0; i < ITERATIONS; i++) {
      getCustomer();
    }
    time = System.currentTimeMillis() - time;
    System.out.println(ITERATIONS * 1000 / time + " Transactions per seconds");
  }

  /**
   * Customer must be fetched in a new transaction, as we might create it
   */
  @Transactional(type = TxType.REQUIRES_NEW)
  private Customer getCustomer() {
    Customer cust = DB.find(Customer.class).setUseQueryCache(true).where().eq("name", "Rob").findOne();
    if (cust == null) {
      // customer not yet initialized, so create it
      cust = new Customer();
      cust.setName("Rob");
      DB.save(cust);
    }
    return cust;
  }


}
