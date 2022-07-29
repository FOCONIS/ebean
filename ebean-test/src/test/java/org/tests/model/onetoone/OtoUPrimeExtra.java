package org.tests.model.onetoone;

import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Formula;

import javax.persistence.*;
import java.util.UUID;

@Entity

public class OtoUPrimeExtra {

  @Id
  UUID eid;

  String extra;

  //@OneToOne//(mappedBy = "extra")
  @OneToOne(optional = false)
  @PrimaryKeyJoinColumn
  //@DbForeignKey(noConstraint = true)
  //@Formula(select = "eid") // funktioniert
  //@JoinColumn(name = "eid")
  private OtoUPrime prime;

  @Version
  Long version;

  public OtoUPrimeExtra() {

  }
  public OtoUPrimeExtra(String extra) {
    this.extra = extra;
  }

  @Override
  public String toString() {
    return "exId:"+ eid +" "+extra;
  }

  public UUID getEid() {
    return eid;
  }

  public void setEid(UUID eid) {
    this.eid = eid;
  }

  public String getExtra() {
    return extra;
  }

  public void setExtra(String extra) {
    this.extra = extra;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public OtoUPrime getPrime() {
    return prime;
  }

  public void setPrime(OtoUPrime prime) {
    this.prime = prime;
  }
}
