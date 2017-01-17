package io.ebean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.ebeaninternal.server.deploy.parse.AnnotationParser;



/**
 * Annotate an entity bean with &#64;CustomAnnotationParser and specify a {@link AnnotationParser} class that is invoked on
 * post process. This class can post-process the DeployBeanDescriptor and generate custom Formulas e.g. 
 * 
 * FIXME: AnnotationParser is ebeaninternal and should be accessed through (not yet existing) interfaces.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomAnnotationParser {

  /**
   * The CustomDeployParser classes
   */
  Class<? extends AnnotationParser>[] value();

}