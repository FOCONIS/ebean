package org.tests.modular;

import io.ebean.annotation.ext.OwnedBy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ModularB {

  @Id
  Long id;

  String value;

  @ManyToOne(optional = false)
  @OwnedBy("modularB")
  ModularA modularA;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ModularA getModularA() {
    return modularA;
  }

  public void setModularA(ModularA modularA) {
    this.modularA = modularA;
  }
}
