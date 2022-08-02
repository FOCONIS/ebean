package io.ebean.bean;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface ExtendableBean {

  ExtensionInfo[] _ebean_getExtensionInfos();

  EntityBean _ebean_getExtension(int index, EntityBeanIntercept ebi);

  public static class ExtensionInfo {
    private final int start;
    private final int length;

    private final int index;
    private final Class type;

    public ExtensionInfo(int start, int length, int index, Class type) {
      this.start = start;
      this.length = length;
      this.index = index;
      this.type = type;
    }

    public int getStart() {
      return start;
    }

    public int getLength() {
      return length;
    }

    public Class getType() {
      return type;
    }

    public int getIndex() {
      return index;
    }

    public EntityBean createInstance(EntityBeanIntercept parentEbi) {
      try {
        EntityBean bean = (EntityBean) type.getConstructor().newInstance();
        Field field = type.getDeclaredField("_ebean_intercept");
        field.setAccessible(true);
        field.set(bean, new ExtendedIntercept(start, parentEbi));
        return bean;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }


}
