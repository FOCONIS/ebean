package org.tests.model.virtualprop;

import io.ebean.bean.EntityBean;
import io.ebean.bean.ExtendableBean;
import org.tests.model.virtualprop.ext.VirtualExtendManyToMany;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roland Praml, FOCONIS AG
 */
@Entity
public class VirtualBase implements ExtendableBean {

  // TOOD: add to enhancer
  public static ExtensionInfo[] _ebean_extensions = new ExtensionInfo[0];

  @Override
  public ExtensionInfo[] _ebean_getExtensions() {
    return _ebean_extensions;
  }
  // TODO: Enhancer end


  @Transient
private Object[] extensionStorage;

  @Override
  public void _ebean_setExtensionStorage(Object[] objects) {
    extensionStorage = objects;
  }

  @Override
  public Object[] _ebean_getExtensionStorage() {
    return extensionStorage;
  }

  @Override
  public <T> T getExtension(Class<T> type) {
      return null;
  }


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
