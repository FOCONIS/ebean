package org.tests.model.mappedsuper;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ASimpleBean extends NotEnhancedMappedSuper {

  @Id
  Long id;

  String name;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
