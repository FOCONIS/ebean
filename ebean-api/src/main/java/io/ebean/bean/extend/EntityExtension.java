package io.ebean.bean.extend;

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

      return ExtensionInfo.get(targetClass).add(sourceProps, sourceClass);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
