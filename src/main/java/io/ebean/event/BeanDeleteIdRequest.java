package io.ebean.event;

import io.ebean.EbeanServer;
import io.ebean.Transaction;

/**
 * A request to delete a bean by Id value.
 */
public interface BeanDeleteIdRequest {

  /**
   * Return the server processing the request.
   */
  EbeanServer getEbeanServer();

  /**
   * Return the Transaction associated with this request.
   */
  Transaction getTransaction();

  /**
   * Returns the bean type of the bean being deleted.
   */
  Class<?> getBeanType();
  
  /**
   * Returns the Id value of the bean being deleted.
   */
  Object getId();

}
