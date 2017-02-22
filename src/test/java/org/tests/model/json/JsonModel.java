package org.tests.model.json;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.tests.model.basic.EBasic;

import io.ebean.annotation.DocStore;

@DocStore
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class JsonModel {

  private String name;
  private String value;
  
  @ManyToOne
  private EBasic basicBean;
  
  @Transient
  String sourceId;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public EBasic getBasicBean() {
    return basicBean;
  }
  
  public void setBasicBean(EBasic basicBean) {
    this.basicBean = basicBean;
  }
  
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }
}
