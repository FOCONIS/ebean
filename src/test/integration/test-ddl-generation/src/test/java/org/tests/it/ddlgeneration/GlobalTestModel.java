package org.tests.it.ddlgeneration;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.ebean.annotation.SharedEntity;


@Entity
@SharedEntity
public class GlobalTestModel {
  @Id
  Integer id;
  
  String name;

  @OneToMany(cascade = CascadeType.PERSIST)
  List<LocalTestModel> localTestModels;

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

  public List<LocalTestModel> getLocalTestModels() {
    return localTestModels;
  }

  public void setLocalTestModels(List<LocalTestModel> localTestModels) {
    this.localTestModels = localTestModels;
  }
  
  

}
