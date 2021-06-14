package io.ebeaninternal.server.querydefn;

import javax.persistence.PersistenceException;

import io.ebean.CancelableQuery;
import io.ebeaninternal.api.SpiCancelableQuery;

/**
 * Common code for Dto/Orm/RelationalQuery
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class AbstractQuery implements SpiCancelableQuery {

  private boolean cancelled;

  private CancelableQuery cancelableQuery;

  @Override
  public void cancel() {
    synchronized (this) {
      if (!cancelled) {
        cancelled = true;
        if (cancelableQuery != null) {
          cancelableQuery.cancel();
        }
      }
    }
  }

  @Override
  public void checkCancelled() {
    if (cancelled) {
      throw new PersistenceException("Query was cancelled");
    }
  }

  @Override
  public void setCancelableQuery(CancelableQuery cancelableQuery) {
    synchronized (this) {
      checkCancelled();
      this.cancelableQuery = cancelableQuery;
    }
  }
}
