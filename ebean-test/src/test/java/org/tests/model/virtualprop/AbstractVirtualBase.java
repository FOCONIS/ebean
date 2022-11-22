package org.tests.model.virtualprop;

import io.ebean.bean.EntityBean;
import io.ebean.bean.extend.ExtendableBean;
import io.ebean.bean.extend.ExtensionInfo;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * @author Roland Praml, FOCONIS AG
 */
@MappedSuperclass
public class AbstractVirtualBase implements ExtendableBean {


  // TOOD: add to enhancer
//  @Transient  public EntityBean[] _ebean_extension_storage;
  // TODO: Enhancer end
  @Id
  private int id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
