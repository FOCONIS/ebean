package io.ebean.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtensionInfo {
  private final int start;
  private final int length;

  private final int index;
  private final EntityBean prototype;

  public ExtensionInfo(int start, int length, int index, Class type) throws ReflectiveOperationException {
    this.start = start;
    this.length = length;
    this.index = index;
    this.prototype = (EntityBean) type.getConstructor().newInstance();
  }

  public int getStart() {
    return start;
  }

  public int getLength() {
    return length;
  }


  public int getIndex() {
    return index;
  }

  public EntityBean createInstance(EntityBeanIntercept parentEbi) {
    return (EntityBean) prototype._ebean_newInstance(new ExtendedIntercept(start,parentEbi));
  }
}
