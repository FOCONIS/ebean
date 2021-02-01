package io.ebeaninternal.server.query;

import io.ebean.FutureSingleAttributeList;
import io.ebean.Query;
import io.ebean.Transaction;

import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Default implementation of FutureIds.
 */
public class QueryFutureSingleAttributeList<T, A> extends BaseFuture<List<A>> implements FutureSingleAttributeList<T, A> {

  private final CallableQuerySingleAttributeList<T, A> call;

  public QueryFutureSingleAttributeList(CallableQuerySingleAttributeList<T, A> call) {
    super(new FutureTask<>(call));
    this.call = call;
  }

  public FutureTask<List<A>> getFutureTask() {
    return futureTask;
  }

  public Transaction getTransaction() {
    return call.transaction;
  }

  @Override
  public Query<T> getQuery() {
    return call.query;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    call.query.cancel();
    return super.cancel(mayInterruptIfRunning);
  }

}
