package org.tests.model.json;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import io.ebean.Ebean;
import io.ebeaninternal.server.core.PersistRequest;


@Entity
public class JsonContainingModel {

  @Id
  Long id;
  
  @Lob
  String jsonModelData;
  
  @Transient
  JsonModel jsonModel;
  
  @Transient
  String sourceId;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getJsonModelData() {
    return jsonModelData;
  }
  
  public void setJsonModelData(String jsonModelData) {
    this.jsonModelData = jsonModelData;
  }
  
  public JsonModel getJsonModel() {
    if (jsonModel == null) {
      String json = getJsonModelData();
      if (json != null) {
        jsonModel = Ebean.json().toBean(JsonModel.class, json);
      }
    }
    return jsonModel;
  }
  
  public void setJsonModel(JsonModel jsonModel) {
    this.jsonModel = jsonModel;
    if (jsonModel == null) {
      setJsonModelData(null);
    }
  }
  
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }
  
  
  public void _ebean_recalc(PersistRequest.Type type) {
    if (jsonModel != null) {
      String json = Ebean.json().toJson(jsonModel);
      if (!json.equals(getJsonModelData())) {
        setJsonModelData(json);
      }
    }
  }
}
