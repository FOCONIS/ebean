package org.tests.it.ddlgeneration;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.annotation.Cache;

@Entity
@Cache
public class LocalCachable {
  @Id
  Integer id;
  
  String name;

  public Integer getId() {
    return id;
  }
  
  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
