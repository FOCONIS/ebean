package org.tests.basic;

import io.ebean.*;
import io.ebean.bean.EntityBean;
import io.ebean.bean.InterceptReadWrite;
import io.ebean.config.DatabaseConfig;
import io.ebean.test.LoggedSql;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.Contact;
import org.tests.model.basic.Country;
import org.tests.model.basic.Customer;
import org.tests.model.basic.ResetBasicData;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCacheIssues extends BaseTestCase {
  private Database db = DB.getDefault();

  /**
   * This demonstrates the "runaway" of a PersistenceContext.
   * We do in a transaction a findOne with enabled queryCache.
   * The bean, that is constructed in the transaction inherits the current persistence context with all the loaded objects
   * (Here: Countries)
   * <p>
   * There are two possible workarounds:
   * - use setDisableLazyLoad: This uses an InterceptReadOnly, which has no persistence context (You may hand craft select/fetches for larger object graphs)
   * - use PersistenceContextScope.QUERY. It seems that QUERY + setReadOnly would do a good job here, but it is a bit cumbersome to add all that query options.
   */
  @Test
  void testPersistenceContextRunaway() {
    ResetBasicData.reset();

    db.cacheManager().clearAll();
    try (Transaction txn = db.beginTransaction()) {
      db.find(Country.class).findList().size(); // load all Country in the PC
      db.find(Customer.class)
        .setUseQueryCache(true)
        .setReadOnly(true)
        //.setDisableLazyLoading(true)
        //.setPersistenceContextScope(PersistenceContextScope.QUERY)
        .where().eq("name", "Rob").findOne();
    }

    Customer rob = db.find(Customer.class)
      .setUseQueryCache(true)
      .setReadOnly(true)
      //.setDisableLazyLoading(true)
      //.setPersistenceContextScope(PersistenceContextScope.QUERY)
      .where().eq("name", "Rob").findOne();

    assertThat(((EntityBean) rob)._ebean_getIntercept()).isInstanceOf(InterceptReadWrite.class);
    // FIXME: This shout be a readonly intercept

    int runaway = ((EntityBean) rob)._ebean_getIntercept().persistenceContext().size(Country.class);
    assertThat(runaway).isEqualTo(2);
    // FIXME: We have 2 countries here in the persistence context
    // they ran away from the transaction above

  }

  @Test
  void testPCPoisioning() {
    ResetBasicData.reset();

    // if we call 'someThirdPartyCodeThatPoisionsPc' before the next 'findEach'
    // it will put one customer in the persistence context. The next db.find
    // will reuse it and may fail, because it tries to modify the bean
    int readOnly = 0;
    try (Transaction txn = db.beginTransaction()) {
      someThirdPartyCodeThatPoisionsPc();
      List<Customer> customers = db.find(Customer.class).findList();
      for (Customer customer : customers) {
        if (db.beanState(customer).isReadOnly()) {
          readOnly++;
        }
      }
    }
    assertThat(readOnly).isEqualTo(1); // FIXME: Here we expect zero

    // Here we do the same, but in different order
    readOnly = 0;
    try (Transaction txn = db.beginTransaction()) {
      List<Customer> customers = db.find(Customer.class).findList();
      someThirdPartyCodeThatPoisionsPc();
      for (Customer customer : customers) {
        if (db.beanState(customer).isReadOnly()) {
          readOnly++;
        }
      }
    }
    assertThat(readOnly).isEqualTo(0);
  }

  private void someThirdPartyCodeThatPoisionsPc() {
    db.find(Customer.class)
      .setReadOnly(true)
      //.setPersistenceContextScope(PersistenceContextScope.QUERY)
      .where().eq("name", "Rob").findOne();
  }

  private Customer getRob() {
    return db.find(Customer.class)
      .setUseQueryCache(true)
      //.setReadOnly(true)
      .where().eq("name", "Rob").findOne();
  }

  /**
   * This demonstrates a dangerous thing, if you modify a bean that is retrieved from the query-cache.
   * <p>
   * It is a good advice, always to use "setReadOnly" in combination with "setUseQueryCache"
   */
  @Test
  void testObjectMutation() {
    ResetBasicData.reset();
    db.cacheManager().clearAll();

    assertThat(getRob().getName()).isEqualTo("Rob");
    assertThat(getRob().getName()).isEqualTo("Rob");

    getRob().setName("Roland");

    assertThat(getRob().getName()).isEqualTo("Roland");

  }


  // ========== the best we can do now ==============

  /**
   * This test case tries to use getCachedCustomer() with the correct options
   */
  @Test
  void testWithCorrectOptions() {
    ResetBasicData.reset();

    List<String> sql = doTest(false, false);
    assertThat(sql).hasSize(3);
    assertThat(sql.get(0)).contains("from o_customer").contains("--bind(Rob)"); // getCachedCustomer
    assertThat(sql.get(1)).contains("from contact"); // lazyLoad of getContacts()
    assertThat(sql.get(2)).contains("from contact").contains(" --bind()"); // Contact.findList
  }

  /**
   * Here we save the bean. Now we have the problem, that after saving the bean, the cache will be skipped
   */
  @Test
  void testWithCorrectOptionsAndSave() {
    ResetBasicData.reset();

    List<String> sql = doTest(true, false);
    assertThat(sql).hasSize(26);
  }

  /**
   * Disabling SkipCacheAfterWrite in the DB-config would save a lot of DB hits.
   */
  @Test
  void testWithCorrectOptionsAndSaveWithoutSkipCacheAfterWrite() {
    ResetBasicData.reset();

    db = createDatabaseWithoutSkipCacheAfterWrite();
    List<String> sql = doTest(true, false);
    assertThat(sql).hasSize(15);
  }

  /**
   * Executing the query in a separate transaction will also allow cache hits.
   */
  @Test
  void testWithSaveAndSeparateTransaction() {
    ResetBasicData.reset();

    List<String> sql = doTest(true, true);
    assertThat(sql).hasSize(15);
  }

  /**
   * This simulates an import.
   */
  private List<String> doTest(boolean save, boolean useSeparateTransaction) {
    db.cacheManager().clearAll();
    LoggedSql.start();
    try (Transaction txn = db.beginTransaction()) {
      Customer cust = useSeparateTransaction ? getCachedCustomerSeparateTransaction() : getCachedCustomer();
      cust.getContacts().get(0).getFirstName();
      List<Contact> contacts = db.find(Contact.class).findList();

      for (Contact contact : contacts) {
        contact.setEmail(computeMail(contact, useSeparateTransaction));
        if (save) {
          db.save(contact);
        }
      }
    }
    List<String> sql = LoggedSql.stop();
    return sql;
  }


  private Customer getCachedCustomer() {
    return db.find(Customer.class)
      .setUseQueryCache(true)
      .setReadOnly(true)
      //.fetch("contacts")
      //.setDisableLazyLoading(true) - we cannot use disableLazyLoad in our app, as we often have a large object graph
      // e.g. we have a "whoCreated" property in each entity and some reports (written in a script language) may
      // try to read "bean.whoCreated.name"
      .setPersistenceContextScope(PersistenceContextScope.QUERY)
      .where().eq("name", "Rob").findOne();
  }

  private Customer getCachedCustomerSeparateTransaction() {
    // Execute everything in a separate transaction
    try (Transaction txn = DB.beginTransaction(TxScope.requiresNew())) {
      return db.find(Customer.class)
        .setUseQueryCache(true)
        .setReadOnly(true)
        .where().eq("name", "Rob").findOne();
    }
  }

  /**
   * We have some third party functions like this, that may require cached beans.
   */
  private String computeMail(Contact contact, boolean useSeparateTransaction) {
    Customer cust = useSeparateTransaction ? getCachedCustomerSeparateTransaction() : getCachedCustomer();
    return contact.getFirstName() + "." + contact.getLastName() + "@" + cust.getName() + ".com";
  }

  private Database createDatabaseWithoutSkipCacheAfterWrite() {
    DatabaseConfig config = new DatabaseConfig();
    config.setName("h2");
    config.loadFromProperties();
    config.setSkipCacheAfterWrite(false);
    config.setRegister(false);
    config.setDefaultServer(false);
    config.setDdlRun(false);
    config.setDdlGenerate(false);

    return DatabaseFactory.create(config);
  }
}
