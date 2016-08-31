package com.avaje.ebean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate a property on an entity bean used to control 'soft delete'
 * (also known as 'logical delete').
 * <p>
 * The property should be of type boolean.
 * </p>
 * <pre>{@code
 *
 * @SoftDelete
 * boolean deleted;
 *
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SoftDelete {
	/**
	 * If set to <code>false</code>, the clause <code>where deleted=false</code>
	 * won't be added to queries, i.e. the evaluation has to be done
	 * programmatically.
	 * <p/>
	 * It is useful in case a ManyToOne property refers to a possibly
	 * soft-deleted entity, which is lazy-loaded. (And you don't want to get an
	 * EntityNotFoundException, if it is indeed soft-deleted.)
	 */
	boolean includeInQueries() default true;
}
