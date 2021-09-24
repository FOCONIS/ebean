package io.ebean.plugin;

import io.ebean.config.dbplatform.DatabasePlatform;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Interface for custom formula annotation implementations. Implementations of this can be used with {@literal @FormulaAlias} to
 * build custom formula constructs.
 *
 * @author Jonas P&ouml;hler, FOCONIS AG
 */
public interface FormulaComputation<M extends Annotation> {

  /**
   * Return the annotation, this computation class is responsible for
   */
  Class<M> supportedAnnotation();

  /**
   * Implement this method to calculate and set a formula for a given property.
   *
   * <b>Example:</b>
   * <pre>
   *   assert annotations.size() == 1;
   *   final ExampleAnnotation annotation = annotations.get(0);
   *
   *   String select = ...;
   *   String join = ...;
   *
   *   prop.setSqlFormula(select, join);
   * </pre>
   *
   * <b>Make sure to always set the formula onto the property at the end of the method implementation</b>
   */
  void compute(final List<M> annotations, final DeployBeanDescriptorMeta descriptor, final DeployBeanPropertyMeta prop,
               final DatabasePlatform platform);

}
