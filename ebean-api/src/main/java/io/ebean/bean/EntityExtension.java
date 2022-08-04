package io.ebean.bean;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface EntityExtension {
  static ExtensionInfo.Entry extend(Class<? extends ExtendableBean> targetClass, Class<? extends EntityExtension> sourceClass) {
    try {
      // append sourceProps to targetProps

      Field sourceField = sourceClass.getField("_ebean_props");

      String[] sourceProps = (String[]) sourceField.get(null);


      //targetField.set(null, newProps);

      return ExtensionInfo.get(targetClass).add(sourceProps, sourceClass);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }



  static <T> T getExtension(ExtendableBean bean, int extensionId) {
    EntityBean eb = (EntityBean) bean;
    return (T) bean._ebean_getExtension(extensionId, eb._ebean_getIntercept());
  }
}
