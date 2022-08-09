package io.ebean.bean.extend;

/**
 * @author Roland Praml, FOCONIS AG
 */
public interface ExtensionAccessor {

  <T> T getExtension(ExtendableBean bean);
}
