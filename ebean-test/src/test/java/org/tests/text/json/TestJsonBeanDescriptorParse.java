package org.tests.text.json;

import com.fasterxml.jackson.core.JsonParser;
import io.ebean.BeanState;
import io.ebean.DB;
import io.ebean.xtest.BaseTestCase;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.api.json.SpiJsonReader;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.json.ReadJson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.Address;
import org.tests.model.basic.Contact;
import org.tests.model.basic.ContactNote;
import org.tests.model.basic.Customer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestJsonBeanDescriptorParse extends BaseTestCase {

  @BeforeEach
  void setup() {
    Customer customer = new Customer();
    customer.setName("Hello Roland");
    customer.setId(234);
    Address address = new Address();
    address.setLine1("foo");
    DB.save(address);
    customer.setBillingAddress(address);

    Contact alfred = new Contact("Alfred", "P");
    alfred.setId(789); // set some deterministic id
    ContactNote note = new ContactNote("Drinks", "Order 100l beer");
    note.setId(890);
    alfred.getNotes().add(note);
    customer.getContacts().add(alfred);
    Contact anton = new Contact("Anton", "P");
    anton.setId(790);
    anton.getNotes().add(new ContactNote("Equipment", "Organize barbecue"));
    customer.getContacts().add(anton);
    DB.save(customer);
    System.out.println(DB.json().toJson(customer));

  }

  @AfterEach
  void teardown() {
    DB.delete(Customer.class, 234);
  }

  @Test
  public void testJsonRead() throws IOException {
    SpiEbeanServer server = (SpiEbeanServer) DB.getDefault();
    BeanDescriptor<Customer> descriptor = server.descriptor(Customer.class);

    SpiJsonReader readJson = createRead(server, descriptor);
    Customer customer = descriptor.jsonRead(readJson, null, null);

    assertEquals(Integer.valueOf(123), customer.getId());
    assertEquals("Hello Rob", customer.getName());

    BeanState beanState = DB.beanState(customer);
    Set<String> loadedProps = beanState.loadedProps();
    assertEquals(2, loadedProps.size());
    assertTrue(loadedProps.contains("id"));
    assertTrue(loadedProps.contains("name"));
    assertThat(beanState.dirtyValues()).isEmpty();
  }

  @Test
  public void testJsonManyUpdate() throws IOException {

    Customer customer = DB.find(Customer.class, 234);
    String json =
      "{\"contacts\": [ "
        + "  {\"id\": 789, \"lastName\": \"Praml\", \"notes\": ["
        + "    {\"id\": 890,\"title\": \"Beer\",\"note\": \"Order 200l beer\",\"version\": 17},"
        + "    {\"title\": \"Food\",\"note\": \"Order 20 steaks\"}"
        + "  ]},"
        + "  {\"id\": 790, \"firstName\": \"Anton\",  \"lastName\": null, \"notes\" : null } "
        + "]}";
    DB.json().toBean(customer, json);
    DB.save(customer);

    customer = DB.find(Customer.class, 234);
    assertThat(customer.getContacts()).hasSize(2);
    customer.getContacts().sort(Comparator.comparing(Contact::getId));

    Contact contact = customer.getContacts().get(0);
    assertThat(contact.getFirstName()).isEqualTo("Alfred");
    assertThat(contact.getLastName()).isEqualTo("Praml");
    assertThat(contact.getNotes()).hasSize(2).extracting(ContactNote::getNote)
      .containsExactlyInAnyOrder("Order 200l beer", "Order 20 steaks");

    contact = customer.getContacts().get(1);
    assertThat(contact.getFirstName()).isEqualTo("Anton");
    assertThat(contact.getLastName()).isEqualTo(null);
    assertThat(contact.getNotes()).hasSize(1);

  }

  @Test
  public void testJsonUpdate() throws IOException {
    Customer customer = DB.find(Customer.class, 234);
    DB.json().toBean(customer, "{}");
    assertFalse(DB.beanState(customer).isDirty());

    assertEquals("Hello Roland", customer.getName());
    DB.json().toBean(customer, "{\"name\":\"Hello Roland\"}");
    assertFalse(DB.beanState(customer).isDirty());

    DB.json().toBean(customer, "{\"name\":\"Hello Rob\"}");
    assertEquals("Hello Rob", customer.getName());
    assertThat(DB.beanState(customer).changedProps()).containsExactly("name");
    assertTrue(DB.beanState(customer).isDirty());
    DB.json().toBean(customer, "{\"name\":null}");
    assertEquals(null, customer.getName());

    assertEquals("foo", customer.getBillingAddress().getLine1());
    DB.json().toBean(customer, "{\"billingAddress\":{\"line1\":\"foo\"}}");
    assertFalse(DB.beanState(customer.getBillingAddress()).isDirty());
    DB.json().toBean(customer, "{\"billingAddress\":{\"line1\":\"bar\"}}");
    assertEquals("bar", customer.getBillingAddress().getLine1());
    assertTrue(DB.beanState(customer.getBillingAddress()).isDirty());

    DB.json().toBean(customer, "{\"contacts\":[{\"firstName\":\"Noemi\"},{\"firstName\":\"Roland\"}]}");
    assertThat(customer.getContacts()).hasSize(2);
    // must not change contacts
    DB.json().toBean(customer, "{}");
    assertThat(customer.getContacts()).hasSize(2);
    DB.json().toBean(customer, "{\"contacts\":[]}");
    assertThat(customer.getContacts()).hasSize(0);


/*
    customer.setName("Hello Roland");
    customer.setId(234);
    Address address = new Address();
    address.setLine1("foo");
    DB.save(address);
    customer.setBillingAddress(address);
    DB.save(customer);


    readJson = createRead(server, descriptor);
    descriptor.jsonRead(readJson, null, customer);
    assertEquals(Integer.valueOf(123), customer.getId());
    assertEquals("Hello Rob", customer.getName());
    assertEquals(123, customer.getId());


    // now check, if toJson is routed over intercept for existing models
    customer = DB.find(Customer.class, 234);
    DB.json().toBean(customer, "{}");
    assertFalse(DB.beanState(customer).isDirty());

    assertEquals("Hello Roland", customer.getName());
    DB.json().toBean(customer, "{\"name\":\"Hello Roland\"}");
    assertFalse(DB.beanState(customer).isDirty());


    DB.json().toBean(customer, "{\"name\":\"Hello Rob\"}");
    assertEquals("Hello Roland", customer.getName());
    assertThat(DB.beanState(customer).changedProps()).containsExactly("name");
    assertTrue(DB.beanState(customer).isDirty());
    DB.json().toBean(customer, "{\"name\":null}");
    assertEquals(null, customer.getName());

    assertEquals("foo", customer.getBillingAddress().getLine2());
    DB.json().toBean(customer, "{\"billingAdress\":{\"line2\":\"foo\"}}");
    assertEquals("foo", customer.getBillingAddress().getLine2());
    DB.delete(customer); // cleanup*/
  }

  private SpiJsonReader createRead(SpiEbeanServer server, BeanDescriptor<Customer> descriptor) {
    StringReader reader = new StringReader("{\"id\":123,\"name\":\"Hello Rob\"}");
    JsonParser parser = server.json().createParser(reader);

    SpiJsonReader readJson = new ReadJson(descriptor, parser, null, null, false);
    return readJson;
  }

}
