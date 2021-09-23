package io.ebean.plugin;

import java.util.Collection;

/**
 * General deployment information. This is used in {@link CustomDeployParser}.
 *
 * @author Roland Praml, FOCONIS AG
 */
public interface DeployBeanDescriptorMeta {

  /**
   * Return a collection of all BeanProperty deployment information.
   */
  Collection<? extends DeployBeanPropertyMeta> propertiesAll();

  /**
   * Get a BeanProperty by its name.
   */
  DeployBeanPropertyMeta getBeanProperty(String secondaryBeanName);

  /**
   * Return the DeployBeanDescriptorMeta for the given bean class.
   */
  DeployBeanDescriptorMeta getDeployBeanDescriptorMeta(Class<?> propertyType);

  /**
   * Return the BeanProperty that make up the unique id.
   */
  DeployBeanPropertyMeta idProperty();

  String getBaseTable();

}
