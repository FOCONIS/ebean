package io.ebeaninternal.server.deploy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.ebean.annotation.Platform;

/**
 * TODO.
 */
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DbTablespace.List.class)
public @interface DbTablespace {

  /**
   * TODO.
   */
  String tablespaceName();
  
  /**
   * TODO.
   */
  String indexTablespace() default "";

  Platform[] platforms() default {};

  /**
   * Repeatable support for {@link DbTablespace}.
   */
  @Target({ElementType.FIELD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {

    DbTablespace[] value() default {};
  }

}
