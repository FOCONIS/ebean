package org.tests.o2m.dm;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class GoodsEntity extends HistoryColumns {
  private String name;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private WorkflowEntity workflowEntity;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public WorkflowEntity getWorkflowEntity() {
    return workflowEntity;
  }

  public void setWorkflowEntity(WorkflowEntity workflowEntity) {
    this.workflowEntity = workflowEntity;
  }
}
