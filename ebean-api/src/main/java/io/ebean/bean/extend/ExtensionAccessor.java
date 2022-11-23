package io.ebean.bean.extend;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface ExtensionAccessor {

  EntityBean createInstance(int offset, EntityBeanIntercept parentEbi);

  <T> T getExtension(ExtendableBean bean);

  int getIndex();

  Class<?> getType();

  String[] getProperties();
}
