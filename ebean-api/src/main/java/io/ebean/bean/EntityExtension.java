package io.ebean.bean;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface EntityExtension {
  static ExtensionInfo.Entry extend(Class<? extends ExtendableBean> targetClass, Class<? extends EntityExtension> sourceClass) {
    try {
      // append sourceProps to targetProps

      Field targetField = targetClass.getField("_ebean_props");
      Field sourceField = sourceClass.getField("_ebean_props");

      String[] targetProps = (String[]) targetField.get(null);
      String[] sourceProps = (String[]) sourceField.get(null);
      String[] newProps = new String[targetProps.length + sourceProps.length];
      System.arraycopy(targetProps, 0, newProps, 0, targetProps.length);
      System.arraycopy(sourceProps, 0, newProps, targetProps.length, sourceProps.length);


      //targetField.set(null, newProps);

      Field field = targetClass.getField("_ebean_extensions");
      ExtensionInfo extensions = (ExtensionInfo) field.get(null);
      if (extensions == null) {
        extensions = new ExtensionInfo(targetProps.length);
        field.set(null, extensions);
      }
      return extensions.add(sourceProps, sourceClass);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static <T> T getExtension(ExtendableBean bean, int extensionId) {
    EntityBean eb = (EntityBean) bean;
    return (T) bean._ebean_getExtension(extensionId, eb._ebean_getIntercept());
  }
}
