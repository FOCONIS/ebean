package org.tests.model.m2m;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class NotificationTarget {
  
  @Id
  private UUID id;
  
  private UUID targetUuid;
  
  @ManyToOne(cascade = {}, fetch = FetchType.LAZY, optional = false)
  private Notification notification;

  public UUID getTargetUuid() {
    return targetUuid;
  }

  public void setTargetUuid(UUID targetUuid) {
    this.targetUuid = targetUuid;
  }

}
