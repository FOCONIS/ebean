package io.ebean.bean.extend;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.bean.NotEnhancedException;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface ExtendableBean {

  default ExtensionInfo _ebean_getExtensionInfo() {
    throw new NotEnhancedException();
  }

  default EntityBean _ebean_getExtension(int index, EntityBeanIntercept ebi) {
    // TODO: Code added by enhancer
    try {
      Field field = getClass().getField("_ebean_extension_storage");
      field.setAccessible(true);
      EntityBean[] extensionStorage = (EntityBean[]) field.get(this);
      if (extensionStorage == null) {
        extensionStorage = new EntityBean[_ebean_getExtensionInfo().size()];
        field.set(this, extensionStorage);
      }
      EntityBean ret = extensionStorage[index];
      if (ret == null) {
        int offset = _ebean_getExtensionInfo().getOffset(index);
        extensionStorage[index] = ret = _ebean_getExtensionInfo().get(index).createInstance(offset, ebi);
      }
      return ret;
    } catch (ReflectiveOperationException re) {
      throw new RuntimeException(re);
    }
  }
}
