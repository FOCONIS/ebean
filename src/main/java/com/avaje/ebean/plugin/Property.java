package com.avaje.ebean.plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Property of a entity bean that can be read.
 */
public interface Property {

  /**
   * Return the name of the property.
   */
  String getName();

  /**
   * Return the value of the property on the given bean.
   */
  Object getVal(Object bean);

  /**
   * Return true if this is a OneToMany or ManyToMany property.
   */
  boolean isMany();
  
  /**
   * Returns a custom attribute for this property.
   */
  <T> T getAttribute(Object key);
  
  /**
   * Sets a custom attribute for this property.  Attributes can be set with a {@link Plugin} at startup.
   */
  void setAttribute(Object key, Object value);
  
  /**
   * Returns the underlying annotatedElement (e.g. the field).
   */
  AnnotatedElement getAnnotatedElement();
}
