package org.tests.model.virtualprop;

import io.ebean.bean.extend.ExtensionInfo;

import javax.persistence.*;

/**
 * @author Roland Praml, FOCONIS AG
 */
@Entity
public class VirtualBase extends AbstractVirtualBase {


  private String data;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
