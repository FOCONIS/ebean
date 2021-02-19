package io.ebean;

import java.util.concurrent.Callable;

/**
 * Wrapper factory for the Background executor. Can be used to transfer thread locals from the calling scope to the async scope.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface BackgroundExecutorWrapper {

  <T> Callable<T> wrap(Callable<T> callable);

  Runnable wrap(Runnable runnable);
}
