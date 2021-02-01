package io.ebeaninternal.server.query;

import io.ebean.Transaction;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.api.SpiQuery;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Represent the fetch Id's query as a Callable.
 *
 * @param <T> the entity bean type
 */
public class CallableQuerySingleAttributeList<T, A> extends CallableQuery<T> implements Callable<List<A>> {


  public CallableQuerySingleAttributeList(SpiEbeanServer server, SpiQuery<T> query, Transaction t) {
    super(server, query, t);
  }

  /**
   * Execute the find Id's query returning the list of Id's.
   */
  @Override
  public List<A> call() {
    // we have already made a copy of the query
    // this way the same query instance is available to the
    // QueryFutureIds (as so has access to the List before it is done)
    try {
      return server.findSingleAttributeList(query, transaction);
    } finally {
      transaction.end();
    }
  }

}
