package io.ebean.metric;

import io.ebean.ProfileLocation;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

/**
 * Factory to create timed metric counters.
 */
public interface MetricFactory {

  /**
   * Return the factory instance.
   */
  static MetricFactory get() {
    return MetricServiceProvider.get();
  }

  /**
   * Create a timed metric group.
   */
  TimedMetricMap createTimedMetricMap(String name);

  /**
   * Create a Timed metric.
   */
  TimedMetric createTimedMetric(String name);

  /**
   * Create a counter metric.
   */
  CountMetric createCountMetric(String name);

  /**
   * Create a metric, that gets the value from a supplier.
   */
  Metric createMetric(String name, LongSupplier supplier);

  /**
   * Create a metric, that gets the value from a supplier.
   */
  Metric createMetric(String name, IntSupplier supplier);

  /**
   * Create a Timed metric.
   */
  QueryPlanMetric createQueryPlanMetric(Class<?> type, String label, ProfileLocation profileLocation, String sql);

}
