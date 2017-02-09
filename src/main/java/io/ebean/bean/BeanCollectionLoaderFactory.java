package io.ebean.bean;

/**
 * Factory that can provide a {@link BeanCollectionLoader} for a certain tenant.
 * 
 * @author Roland Praml, FOCONIS AG
 */
public interface BeanCollectionLoaderFactory {

  /**
   * Return a {@link BeanCollectionLoader} for the given tenant.
   */
  BeanCollectionLoader getBeanCollectionLoader(Object tenantId);

  /**
   * Return a {@link BeanCollectionLoader} for current tenant.
   */
  BeanCollectionLoader getBeanCollectionLoader();
  
}
