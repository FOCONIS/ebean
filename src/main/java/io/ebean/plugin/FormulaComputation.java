package io.ebean.plugin;

import io.ebean.config.dbplatform.DatabasePlatform;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Jonas P&ouml;hler, FOCONIS AG
 */
public interface FormulaComputation<M extends Annotation> {

  Class<M> supportedAnnotation();

  void compute(final List<M> annotations, final DeployBeanDescriptorMeta descriptor, final DeployBeanPropertyMeta prop,
               final DatabasePlatform platform);

}
