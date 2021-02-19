package io.ebeaninternal.server.core;

import io.ebean.BackgroundExecutorWrapper;
import io.ebeaninternal.api.SpiBackgroundExecutor;
import io.ebeaninternal.server.lib.DaemonExecutorService;
import io.ebeaninternal.server.lib.DaemonScheduleThreadPool;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The default implementation of the BackgroundExecutor.
 */
public class DefaultBackgroundExecutor implements SpiBackgroundExecutor {

  private final DaemonScheduleThreadPool schedulePool;

  private final DaemonExecutorService pool;

  private final BackgroundExecutorWrapper executionWrapper;

  /**
   * Construct the default implementation of BackgroundExecutor.
   */
  public DefaultBackgroundExecutor(int schedulePoolSize, int shutdownWaitSeconds, String namePrefix, BackgroundExecutorWrapper executionWrapper) {
    this.pool = new DaemonExecutorService(shutdownWaitSeconds, namePrefix);
    this.schedulePool = new DaemonScheduleThreadPool(schedulePoolSize, shutdownWaitSeconds, namePrefix + "-periodic-");
    this.executionWrapper = executionWrapper;
  }

  /**
   * Execute a Runnable using a background thread.
   */
  @Override
  public void execute(Runnable r) {
    Runnable wrapped = executionWrapper == null ? r : executionWrapper.wrap(r);
    pool.execute(wrapped);
  }

  @Override
  public void executePeriodically(Runnable r, long delay, TimeUnit unit) {
    Runnable wrapped = executionWrapper == null ? r : executionWrapper.wrap(r);
    schedulePool.scheduleWithFixedDelay(wrapped, delay, delay, unit);
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {
    Runnable wrapped = executionWrapper == null ? r : executionWrapper.wrap(r);
    return schedulePool.schedule(wrapped, delay, unit);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> c, long delay, TimeUnit unit) {
    Callable<V> wrapped = executionWrapper == null ? c : executionWrapper.wrap(c);
    return schedulePool.schedule(wrapped, delay, unit);
  }

  @Override
  public void shutdown() {
    pool.shutdown();
    schedulePool.shutdown();
  }

}
