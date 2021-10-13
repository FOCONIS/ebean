package io.ebeaninternal.server.persist;

import io.ebean.EbeanServer;
import io.ebean.Transaction;
import io.ebean.event.BeanDeleteIdRequest;
import io.ebeaninternal.api.SpiEbeanServer;

class DeleteIdRequest implements BeanDeleteIdRequest {

  private final EbeanServer server;
  private final Transaction transaction;
  private Class<?> beanType;
  private Object id;

  DeleteIdRequest(SpiEbeanServer server, Transaction transaction, Class<?> beanType, Object id) {
    this.server = server;
    this.transaction = transaction;
    this.beanType = beanType;
    this.id = id;
  }

  void setId(Object id) {
    this.id = id;
  }

  @Override
  public EbeanServer getEbeanServer() {
    return server;
  }

  @Override
  public Transaction getTransaction() {
    return transaction;
  }

  @Override
  public Class<?> getBeanType() {
    return beanType;
  }

  @Override
  public Object getId() {
    return id;
  }
}
