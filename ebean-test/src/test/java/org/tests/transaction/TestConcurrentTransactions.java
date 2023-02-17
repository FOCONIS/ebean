package org.tests.transaction;

import io.avaje.applog.AppLog;
import io.ebean.DB;
import io.ebean.Transaction;
import io.ebean.TransactionCallbackAdapter;
import io.ebean.annotation.Platform;
import io.ebean.xtest.BaseTestCase;
import io.ebean.xtest.ForPlatform;
import org.junit.jupiter.api.Test;
import org.tests.model.info.InfoCompany;
import org.tests.model.info.InfoContact;
import org.tests.model.info.InfoCustomer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jonas Pöhler, FOCONIS AG
 */
public class TestConcurrentTransactions extends BaseTestCase {

  private static final System.Logger log = AppLog.getLogger(TestConcurrentTransactions.class);

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @ForPlatform({Platform.MARIADB, Platform.H2})
  public void testConcurrentTransactions() throws ExecutionException, InterruptedException {
    InfoCustomer customer1 = new InfoCustomer();
    customer1.setName("model1");
    customer1.setId(1L);
    customer1.save();

    InfoCustomer customer2 = new InfoCustomer();
    customer2.setName("model2");
    customer2.setId(2L);
    customer2.save();

    InfoCompany company1 = new InfoCompany();
    company1.setName("foo");
    company1.save();

    InfoContact contact1 = new InfoContact();
    contact1.setName("contact1");
    contact1.setId(1L);
    contact1.setCompany(company1);
    contact1.save();

    InfoCompany company2 = new InfoCompany();
    company2.setName("foo");
    company2.save();

    InfoContact contact2 = new InfoContact();
    contact2.setName("contact2");
    contact2.setId(2L);
    contact2.setCompany(company2);
    contact2.save();

    Runnable r1 = () -> {
      InfoCompany company3 = new InfoCompany();
      try (Transaction txn = DB.beginTransaction()) {
        sleep(50);

        log.log(System.Logger.Level.INFO, "updating customer to foo");
        DB.update(InfoCustomer.class).set("name", "foo").where().idEq(1L).update();
        log.log(System.Logger.Level.INFO, "updated customer to foo");
        txn.register(new TransactionCallbackAdapter() {
          @Override
          public void preCommit() {
            sleep(200);

            log.log(System.Logger.Level.INFO, "updating contact to foo");
            DB.update(InfoContact.class).set("name", "foo").where().idEq(1L).update();
            log.log(System.Logger.Level.INFO, "updated contact to foo");
          }
        });

        company3.setName("company3");
        company3.setId(3L);
        company3.save();

        txn.commit();
      }
    };
    Runnable r2 = () -> {
      InfoCompany company4 = new InfoCompany();
      try (Transaction txn = DB.beginTransaction()) {
        sleep(50);

        log.log(System.Logger.Level.INFO, "updating contact to baz");
        DB.update(InfoContact.class).set("name", "baz").where().idEq(1L).update();
        log.log(System.Logger.Level.INFO, "updated contact to baz");

        txn.register(new TransactionCallbackAdapter() {
          @Override
          public void preCommit() {
            sleep(100);

            log.log(System.Logger.Level.INFO, "updating customer to baz");
            DB.update(InfoCustomer.class).set("name", "baz").where().idEq(1L).update();
            log.log(System.Logger.Level.INFO, "updated customer to baz");
          }
        });

        company4.setName("company4");
        company4.setId(4L);
        company4.save();

        txn.commit();
      }
    };

    Future<?> future1 = DB.backgroundExecutor().submit(r1);
    Future<?> future2 = DB.backgroundExecutor().submit(r2);

    future1.get();
    future2.get();

    assertTrue(DB.find(InfoCompany.class).where().eq("name", "company3").exists());
    assertTrue(DB.find(InfoCompany.class).where().eq("name", "company4").exists());
  }

}
