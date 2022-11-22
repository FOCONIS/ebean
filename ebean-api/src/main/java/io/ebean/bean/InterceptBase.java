package io.ebean.bean;

import io.ebean.bean.extend.ExtendableBean;
import io.ebean.bean.extend.ExtensionInfo;

/**
 * Base class for InterceptReadOnly / InterceptReadWrite. This class should contain only the essential member variables to keep
 * the memory footprint low.
 *
 * @author Roland Praml, FOCONIS AG
 */
public abstract class InterceptBase implements EntityBeanIntercept {

  /**
   * The actual entity bean that 'owns' this intercept.
   */
  protected final EntityBean owner;

  protected InterceptBase(EntityBean owner) {
    this.owner = owner;
  }

  protected ExtensionInfo extensionInfo() {
    // Checkme: Cache in local field
    if (owner instanceof ExtendableBean) {
      return ((ExtendableBean) owner)._ebean_getExtensionInfo();
    } else {
      return ExtensionInfo.NONE;
    }
  }

  protected EntityBean getExtensionBean(ExtensionInfo.Entry entry) {
    ExtendableBean eb = (ExtendableBean) owner;
    return eb._ebean_getExtension(entry.getIndex(), this);
  }

  @Override
  public int findProperty(String propertyName) {
    String[] names = owner._ebean_getPropertyNames();
    for (int i = 0; i < names.length; i++) {
      if (names[i].equals(propertyName)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public String getProperty(int propertyIndex) {
    if (propertyIndex == -1) {
      return null;
    }
    ExtensionInfo.Entry entry = extensionInfo().findEntry(propertyIndex);
    if (entry == null) {
      return owner._ebean_getPropertyName(propertyIndex);
    } else {
      int offset = extensionInfo().getOffset(entry);
      return getExtensionBean(entry)._ebean_getPropertyName(propertyIndex - offset);
    }
  }

  @Override
  public int getPropertyLength() {
    return owner._ebean_getPropertyNames().length + extensionInfo().getPropertyLength();
  }

  @Override
  public Object getValue(int index) {
    ExtensionInfo.Entry entry = extensionInfo().findEntry(index);
    if (entry == null) {
      return owner._ebean_getField(index);
    } else {
      int offset = extensionInfo().getOffset(entry);
      return getExtensionBean(entry)._ebean_getField(index - offset);
    }
  }

  @Override
  public Object getValueIntercept(int index) {
    ExtensionInfo.Entry entry = extensionInfo().findEntry(index);
    if (entry == null) {
      return owner._ebean_getFieldIntercept(index);
    } else {
      int offset = extensionInfo().getOffset(entry);
      return getExtensionBean(entry)._ebean_getFieldIntercept(index - offset);
    }
  }

  @Override
  public void setValue(int index, Object value) {
    ExtensionInfo.Entry entry = extensionInfo().findEntry(index);
    if (entry == null) {
      owner._ebean_setField(index, value);
    } else {
      int offset = extensionInfo().getOffset(entry);
      getExtensionBean(entry)._ebean_setField(index - offset, value);
    }
  }

  @Override
  public void setValueIntercept(int index, Object value) {
    ExtensionInfo.Entry entry = extensionInfo().findEntry(index);
    if (entry == null) {
      owner._ebean_setFieldIntercept(index, value);
    } else {
      int offset = extensionInfo().getOffset(entry);
      getExtensionBean(entry)._ebean_setFieldIntercept(index - offset, value);
    }
  }
}
