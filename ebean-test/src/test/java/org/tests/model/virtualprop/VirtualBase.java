package org.tests.model.virtualprop;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.bean.ExtendableBean;
import io.ebean.bean.ExtensionInfo;

import javax.persistence.*;

/**
 * @author Roland Praml, FOCONIS AG
 */
@Entity
public class VirtualBase implements ExtendableBean {

  // TOOD: add to enhancer
  public static ExtensionInfo _ebean_extensions;
  @Transient  private EntityBean[] _ebean_extension_storage;
  // TODO: Enhancer end



  @Id
  private int id;

  private String data;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
