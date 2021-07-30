package org.tests.model.m2m;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Notification {
  
  @Id
  private Integer id;
  
  private Integer typeId;
  
  private String refTableName;
  
  @OneToMany(mappedBy = "notification", cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
  private List<NotificationTarget> targets = new ArrayList<>();

  public Integer getTypeId() {
    return typeId;
  }

  public void setTypeId(Integer typeId) {
    this.typeId = typeId;
  }
  
  public List<NotificationTarget> getTargets() {
    return targets;
  }
  
  public void setTargets(List<NotificationTarget> targets) {
    this.targets = targets;
  }

  public String getRefTableName() {
    return refTableName;
  }

  public void setRefTableName(String refTableName) {
    this.refTableName = refTableName;
  }

}
