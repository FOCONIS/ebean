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
    throw new NotEnhancedException();
  }
}
