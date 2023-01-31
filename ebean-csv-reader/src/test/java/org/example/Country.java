package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Country {

  @Id
  String code;

  String name;

  public String code() {
    return code;
  }

  public Country code(String code) {
    this.code = code;
    return this;
  }

  public String name() {
    return name;
  }

  public Country name(String name) {
    this.name = name;
    return this;
  }
}
