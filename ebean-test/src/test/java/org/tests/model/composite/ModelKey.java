package org.tests.model.composite;

import javax.persistence.Embeddable;
import java.util.UUID;

/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 * @author
 */
@Embeddable
public class ModelKey {

  @io.ebean.annotation.NotNull
  private UUID fromId;

  @io.ebean.annotation.NotNull
  private UUID toId;

  public UUID getFromId() {
    return fromId;
  }

  public void setFromId(UUID fromId) {
    this.fromId = fromId;
  }

  public UUID getToId() {
    return toId;
  }

  public void setToId(UUID toId) {
    this.toId = toId;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;

    return hash;
  }

}
