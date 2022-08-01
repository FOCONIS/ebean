package org.tests.model.virtualprop;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import java.lang.annotation.*;

/**
 * Annotation for a class to extend an existing class.
 *
 * @author Alexander Wagner, FOCONIS AG
 */
@Documented
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface VirtualManyToMany {

  /**
   * The property name, that should be created in TargetEntity
   */
	String propertyName() default "";

  /**
   * The fetch type of that virtual property
   */
	FetchType fetch() default FetchType.LAZY;

  /**
   * The cascade info.
   */
	CascadeType[] cascade() default { CascadeType.ALL };

}
