package io.ebean.plugin;

import io.ebean.config.dbplatform.DatabasePlatform;

import java.lang.annotation.Annotation;

/**
 * @author Jonas P&ouml;hler, FOCONIS AG
 */
public interface FormulaComputation {

  void compute(final Annotation annotation, final DeployBeanDescriptorMeta descriptor, final DeployBeanPropertyMeta prop, final DatabasePlatform platform);

}
