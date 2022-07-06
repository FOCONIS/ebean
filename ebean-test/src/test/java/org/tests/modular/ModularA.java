package org.tests.modular;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ModularA {

  @Id
  Long id;

  String content;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
