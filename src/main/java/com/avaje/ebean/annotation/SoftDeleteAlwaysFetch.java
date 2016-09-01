package com.avaje.ebean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.EntityNotFoundException;

/**
 * If a model is annotated with <code>{@literal @}SoftDeleteAlwaysFetch</code>,
 * the "soft-delete condition" (e.g. <code>where deleted=false</code>) won't be added to
 * queries referring to this model.
 * <p/>
 * It is especially useful in case an <code>{@literal @}ManyToOne</code> property refers to a possibly
 * soft-deleted entity, which is lazy-loaded. (And you don't want to get an
 * {@link EntityNotFoundException}, if it is indeed soft-deleted.)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SoftDeleteAlwaysFetch {

}
