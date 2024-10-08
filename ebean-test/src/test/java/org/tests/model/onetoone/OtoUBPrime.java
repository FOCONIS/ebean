package org.tests.model.onetoone;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import java.util.UUID;

@Entity
public class OtoUBPrime {

  @Id
  UUID pid;

  String name;

  /**
   * Master side of bi-directional PrimaryJoinColumn.
   */
  @OneToOne(mappedBy = "prime", optional = false)
  OtoUBPrimeExtra extra;

  @Version
  Long version;

  public OtoUBPrime(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "id:"+ pid +" name:"+name+" extra:"+extra;
  }

  public UUID getPid() {
    return pid;
  }

  public void setPid(UUID pid) {
    this.pid = pid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OtoUBPrimeExtra getExtra() {
    return extra;
  }

  public void setExtra(OtoUBPrimeExtra extra) {
    this.extra = extra;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}
