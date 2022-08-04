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
  public static ExtensionInfo _ebean_extensions = new ExtensionInfo(AbstractVirtualBase.class, null);
  @Transient  public EntityBean[] _ebean_extension_storage;
  // TODO: Enhancer end

  @Override
  public ExtensionInfo _ebean_getExtensionInfos() {
    return _ebean_extensions;
  }
  @Id
  private int id;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
