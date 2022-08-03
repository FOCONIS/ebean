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
  private final Class type;

  public ExtensionInfo(int start, int length, int index, Class type) throws ReflectiveOperationException {
    this.start = start;
    this.length = length;
    this.index = index;
    this.type = type;
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

  public Class getType() {
    return type;
  }

  public EntityBean createInstance(EntityBeanIntercept parentEbi) {
    return (EntityBean) prototype._ebean_newInstance(new ExtendedIntercept(start,parentEbi));
  }

  public static int extend(Class targetClass, Class sourceClass) {
    try {
      // append sourceProps to targetProps

      Field targetField = targetClass.getField("_ebean_props");
      Field sourceField = sourceClass.getField("_ebean_props");

      String[] targetProps = (String[]) targetField.get(null);
      String[] sourceProps = (String[]) sourceField.get(null);
      String[] newProps = new String[targetProps.length + sourceProps.length];
      System.arraycopy(targetProps, 0, newProps, 0, targetProps.length);
      System.arraycopy(sourceProps, 0, newProps, targetProps.length, sourceProps.length);


      targetField.set(null, newProps);
      sourceField.set(null, new String[0]);

      Field field = targetClass.getField("_ebean_extensions");
      ExtensionInfo[] extensions = (ExtensionInfo[]) field.get(null);
      ExtensionInfo[] newExtensionInfo = new ExtensionInfo[extensions.length+1];
      System.arraycopy(extensions, 0, newExtensionInfo, 0, extensions.length);
      newExtensionInfo[extensions.length] = new ExtensionInfo(targetProps.length, sourceProps.length, extensions.length, sourceClass);
      field.set(null, newExtensionInfo);

      return extensions.length;

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
