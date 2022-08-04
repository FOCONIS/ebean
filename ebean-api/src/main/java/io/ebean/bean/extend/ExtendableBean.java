package io.ebean.bean.extend;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface ExtendableBean {

  ExtensionInfo _ebean_getExtensionInfos();

  default EntityBean _ebean_getExtension(int index, EntityBeanIntercept ebi) {
    // TODO: Code added by enhancer
    try {
      Field field = getClass().getField("_ebean_extension_storage");
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
