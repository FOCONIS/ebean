package org.tests.model.docstore;

import io.ebean.annotation.DocStore;
import io.ebean.annotation.JsonIgnore;
import org.tests.model.basic.Customer;

import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@DocStore
public class ReportComment {

  private String comment;

  @ManyToOne
  private Customer author;

  @Transient
  @JsonIgnore
  private Object parentBean;

  @Transient
  @JsonIgnore
  private String propertyName;

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Customer getAuthor() {
    return author;
  }

  public void setAuthor(Customer author) {
    this.author = author;
  }

  public Object getParentBean() {
    return parentBean;
  }

  public String getPropertyName() {
    return propertyName;
  }
}
