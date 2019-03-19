package org.tests.model.basic.cache;

import io.ebean.annotation.Cache;
import io.ebean.annotation.DbJson;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;

@Cache
@Entity
@Inheritance
@DiscriminatorValue("T")
public class CInhTwo extends CInhRoot {
  private static final long serialVersionUID = 5535529382231178815L;

  private String action;

  @DbJson
  private Map<String, String> map = new HashMap<>();

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Map<String, String> getMap() {
    return map;
  }
}
