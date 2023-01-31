package org.example.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;

@Inheritance
@DiscriminatorValue("CAT")
@Entity
public class ACat extends Animal {

  public ACat(String name) {
    super(name);
  }

}
