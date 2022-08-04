package org.tests.model.virtualprop;

import io.ebean.bean.extend.ExtensionInfo;

import javax.persistence.*;

/**
 * @author Roland Praml, FOCONIS AG
 */
@Entity
public class VirtualBase extends AbstractVirtualBase {

  // TOOD: add to enhancer
  public static ExtensionInfo _ebean_extensions = new ExtensionInfo(VirtualBase.class, AbstractVirtualBase._ebean_extensions);
  // TODO: Enhancer end


  @Override
  public ExtensionInfo _ebean_getExtensionInfos() {
      return _ebean_extensions;
  }

  private String data;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
