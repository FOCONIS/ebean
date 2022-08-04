package io.ebean.bean;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface ExtendableBean {

  default ExtensionInfo _ebean_getExtensionInfos() {
    try {
      Field field = getClass().getDeclaredField("_ebean_extensions");
      field.setAccessible(true);
      return (ExtensionInfo) field.get(null);
    } catch (ReflectiveOperationException re) {
      throw new RuntimeException(re);
    }
  }

  default EntityBean _ebean_getExtension(int index, EntityBeanIntercept ebi) {
    // TODO: Code added by enhancer
    try {
      Field field = getClass().getDeclaredField("_ebean_extension_storage");
      field.setAccessible(true);
      EntityBean[] extensionStorage = (EntityBean[]) field.get(this);
      if (extensionStorage == null) {
        extensionStorage = new EntityBean[_ebean_getExtensionInfos().size()];
        field.set(this, extensionStorage);
      }
      EntityBean ret = extensionStorage[index];
      if (ret == null) {
        extensionStorage[index] = ret = _ebean_getExtensionInfos().get(index).createInstance(ebi);
      }
      return ret;
    } catch (ReflectiveOperationException re) {
      throw new RuntimeException(re);
    }
  }
}
