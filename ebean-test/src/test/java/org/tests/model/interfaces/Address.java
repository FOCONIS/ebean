package org.tests.model.interfaces;

import io.ebean.annotation.ext.EntityImplements;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;

@Entity
@EntityImplements(IAddress.class)
public class Address implements IAddress {

  @Id
  private long oid;

  @Version
  private int version;

  private String street;

  @ManyToOne
  private Person extraAddress;

  public Address(String street) {
    this.street = street;
  }

  public long getOid() {
    return oid;
  }

  public void setOid(long oid) {
    this.oid = oid;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public String getStreet() {
    return street;
  }

  @Override
  public void setStreet(String s) {
    this.street = s;
  }

}
