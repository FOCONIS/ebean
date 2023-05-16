package io.ebeaninternal.api;

import io.ebean.meta.MetaQueryPlan;

import java.time.Instant;

/**
 * Internal database query plan being capture.
 */
public interface SpiDbQueryPlan extends MetaQueryPlan {

  /**
   * Extend with queryTimeMicros and captureCount.
   */
  SpiDbQueryPlan with(long queryTimeMicros, long captureCount, long captureMicros, Instant whenCaptured);

}
