package org.tests.batchload;

import io.ebean.DB;
import io.ebean.common.BeanList;
import io.ebean.common.BeanListLazyAdd;
import io.ebean.test.LoggedSql;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.Contact;
import org.tests.model.basic.Customer;
import org.tests.order.OrderMaster;
import org.tests.order.OrderReferencedChild;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestLazyAddBeanList extends BaseTestCase {

  private Customer cust;
  private OrderMaster orderMaster;

  @BeforeEach
  void init() {
    cust = new Customer();
    cust.setName("noFetch");
    DB.save(cust);
    cust = DB.find(Customer.class, cust.getId()); // fetch fresh from DB

    orderMaster = new OrderMaster();
    DB.save(orderMaster);
    orderMaster = DB.find(OrderMaster.class, orderMaster.getId());
  }

  @AfterEach
  void tearDown() {
    DB.delete(cust);
    DB.delete(orderMaster);
  }

  @Test
  public void testCollectionType() {
    assertThat(cust.getContacts())
      .isInstanceOf(BeanListLazyAdd.class);

    assertThat(orderMaster.getChildren())
      .isInstanceOf(BeanList.class)
      .isNotInstanceOf(BeanListLazyAdd.class);

    assertThat(DB.reference(Customer.class, 1).getContacts())
      .isInstanceOf(BeanListLazyAdd.class);

    assertThat(DB.reference(OrderMaster.class, 1).getChildren())
      .isInstanceOf(BeanList.class)
      .isNotInstanceOf(BeanListLazyAdd.class);
  }

  @Test
  public void testNoFetch() {

    LoggedSql.start();
    cust.addContact(new Contact("jim", "slim"));
    cust.addContact(new Contact("joe", "big"));
    DB.save(cust);

    List<String> sql = LoggedSql.stop();
    assertThat(sql.get(0)).contains("insert into contact");
    assertThat(sql.get(1)).contains("bind(jim,slim,");
    assertThat(sql.get(2)).contains("bind(joe,big,");
    assertThat(sql).hasSize(3);

    List<String> list = DB.find(Customer.class)
      .fetch("contacts", "firstName")
      .where()
      .idEq(cust.getId()).findSingleAttributeList();
// check if it is really saved
    assertThat(list).containsExactlyInAnyOrder("jim", "joe");
  }

  @Test
  public void testFetch() {

    LoggedSql.start();
    orderMaster.getChildren().add(new OrderReferencedChild("foo"));
    orderMaster.getChildren().add(new OrderReferencedChild("bar"));
    DB.save(orderMaster);

    List<String> sql = LoggedSql.stop();

    assertThat(sql.get(0)).contains("from order_referenced_parent"); // lazy load children
    assertThat(sql.get(1)).contains("insert into order_referenced_parent");
    assertThat(sql.get(2)).contains("bind(D,foo,");
    assertThat(sql.get(3)).contains("bind(D,bar,");

    List<String> list = DB.find(OrderMaster.class)
      .fetch("children", "name")
      .where().idEq(orderMaster.getId())
      .findSingleAttributeList();
    // check if it is really saved
    assertThat(list).containsExactlyInAnyOrder("foo", "bar");
  }

  @Test
  public void testAddToExisting() {

    // add some existing entries
    LoggedSql.start();
    cust.getContacts().addAll(Arrays.asList(
      new Contact("jim", "slim"),
      new Contact("joe", "big")));
    assertThat(LoggedSql.stop()).isEmpty();

    LoggedSql.start();
    DB.save(cust);
    assertThat(LoggedSql.stop()).hasSize(3); // insert + 2x bind

    cust = DB.find(Customer.class, cust.getId());

    LoggedSql.start();
    List<Contact> contacts = cust.getContacts();
    contacts.add(new Contact("charlie", "brown"));
    assertThat(LoggedSql.stop()).isEmpty();

    LoggedSql.start();
    assertThat(contacts).
      extracting(Contact::getFirstName).
      containsExactlyInAnyOrder("jim", "joe", "charlie");
    List<String> sql = LoggedSql.stop();

    assertThat(sql.get(0)).contains("from o_customer t0 left join contact t1 ");
    assertThat(sql).hasSize(1);
  }

}
