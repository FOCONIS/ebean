package org.tests.model.basic.cache;

import io.ebean.annotation.Cache;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Entity
@Cache(enableQueryCache = true, enableBeanCache = false)
public class ECacheRoot {

  @Id
  @GeneratedValue
  protected UUID id;

  @Size(max = 100)
  private String name;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
