package org.tests.basic;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import org.tests.model.basic.ResetBasicData;
import org.tests.model.interfaces.Address;
import org.tests.model.interfaces.ExtPerson1and2;
import org.tests.model.interfaces.IAddress;
import org.tests.model.interfaces.IExtPerson1;
import org.tests.model.interfaces.IExtPerson2;
import org.tests.model.interfaces.IPerson;
import org.tests.model.interfaces.Person;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestManyOneInterface extends BaseTestCase {

  private static class MyAddress implements IAddress {

    private long oid;
    private String street;

     MyAddress(long oid) {
      this.oid = oid;
    }

    @Override
    public long getOid() {
      return oid;
    }

    @Override
    public String getStreet() {
      return street;
    }

    @Override
    public void setStreet(String street) {
      this.street = street;
    }
  }
  @Test
  public void test() {

    ResetBasicData.reset();

    IAddress a = Ebean.getDefaultServer().getPluginApi().createEntityBean(IAddress.class);
    assertThat(a).isInstanceOf(Address.class);

    IPerson p = Ebean.getDefaultServer().getPluginApi().createEntityBean(Person.class);
    assertThat(p).isInstanceOf(ExtPerson1and2.class);
    p = Ebean.getDefaultServer().getPluginApi().createEntityBean(IPerson.class);
    assertThat(p).isInstanceOf(ExtPerson1and2.class);

    p.setDefaultAddress(a);


    IAddress ea1 = Ebean.getDefaultServer().getPluginApi().createEntityBean(IAddress.class);
    IAddress ea2 = Ebean.getDefaultServer().getPluginApi().createEntityBean(IAddress.class);
//    Ebean.save(ea1);
//    Ebean.save(ea2);

    p.getExtraAddresses().add(ea1);
    p.getExtraAddresses().add(ea2);

    Ebean.save(p);


    IAddress a2 = Ebean.find(IAddress.class, a.getOid());
    IPerson p2 = Ebean.find(IPerson.class, p.getOid());

    assertThat(a2).isInstanceOf(Address.class);
    assertThat(p2).isInstanceOf(ExtPerson1and2.class);

    //assertThat(a2.getPersons().get(0)).isInstanceOf(ExtPerson1and2.class);
    assertThat(p2.getDefaultAddress()).isInstanceOf(Address.class);

    // some more checks
    IExtPerson1 pe1 = Ebean.getDefaultServer().getPluginApi().createEntityBean(IExtPerson1.class);
    IExtPerson2 pe2 = Ebean.getDefaultServer().getPluginApi().createEntityBean(IExtPerson2.class);
    assertThat(pe1).isInstanceOf(ExtPerson1and2.class);
    assertThat(pe2).isInstanceOf(ExtPerson1and2.class);



    IPerson p3 = Ebean.getDefaultServer().getPluginApi().createEntityBean(Person.class);
    IAddress a3 = new MyAddress(a.getOid());
    p3.setDefaultAddress(a3);

    ea1 = new MyAddress(ea1.getOid());
    ea2 = new MyAddress(ea2.getOid());
    p3.getAddressLinks().add(ea1);
    p3.getAddressLinks().add(ea2);
    Ebean.save(p3);
  }
}
