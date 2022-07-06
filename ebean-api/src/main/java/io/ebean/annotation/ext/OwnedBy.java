package io.ebean.annotation.ext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * FIXME: Was brauchen wir eigentlich und wie soll das Ding heißen?
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface OwnedBy {

  String value();

}
