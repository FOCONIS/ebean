package io.ebean.bean;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface ExtendableBean {
  void _ebean_setExtensionStorage(Object[] objects);

  Object[] _ebean_getExtensionStorage();

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

    public Object createInstance(EntityBeanIntercept parentEbi) {
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

  <T> T getExtension(Class<T> type);

  ExtensionInfo[] _ebean_getExtensions();

}
