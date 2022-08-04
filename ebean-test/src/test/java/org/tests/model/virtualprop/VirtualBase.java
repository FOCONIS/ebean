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
public class VirtualBase extends AbstractVirtualBase {

  // TOOD: add to enhancer
  public static ExtensionInfo _ebean_extensions;
  // TODO: Enhancer end

  private String data;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
