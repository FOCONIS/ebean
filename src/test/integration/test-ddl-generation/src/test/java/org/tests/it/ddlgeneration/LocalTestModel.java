package org.tests.it.ddlgeneration;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "local_model")
public class LocalTestModel {
  @Id
  Integer id;
  
  @ManyToOne
  GlobalTestModel globalTestModel;
  
  String name;
  
  public Integer getId() {
    return id;
  }
  
  public void setId(Integer id) {
    this.id = id;
  }


  public GlobalTestModel getGlobalTestModel() {
    return globalTestModel;
  }

  public void setGlobalTestModel(GlobalTestModel globalTestModel) {
    this.globalTestModel = globalTestModel;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  
}
