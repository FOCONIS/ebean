package org.tests.model.composite;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 * @author
 */
@Entity
public class Model {

  @EmbeddedId
  private ModelKey id;

  private String description;

  @ManyToOne
  private ModelSubEntity from;

  @ManyToOne
  private ModelSubEntity to;

  public Model() {

  }

  public ModelKey getId() {
    return id;
  }

  public void setId(ModelKey id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ModelSubEntity getFrom() {
    return from;
  }

  public void setFrom(ModelSubEntity from) {
    this.from = from;
  }

  public ModelSubEntity getTo() {
    return to;
  }

  public void setTo(ModelSubEntity to) {
    this.to = to;
  }

}
