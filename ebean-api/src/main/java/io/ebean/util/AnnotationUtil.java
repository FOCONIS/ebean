package io.ebean.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Annotation utility methods to find annotations.
 */
public class AnnotationUtil {

  /**
   * Determine if the supplied {@link Annotation} is defined in the core JDK {@code java.lang.annotation} package.
   */
  public static boolean notJavaLang(Annotation annotation) {
    return !annotation.annotationType().getName().startsWith("java.lang.annotation");
  }

  /**
   * Simple get on field or method with no meta-annotations or platform filtering.
   */
  public static <A extends Annotation> A get(AnnotatedElement element, Class<A> annotation) {
    return element.getAnnotation(annotation);
  }

  /**
   * Simple has with no meta-annotations or platform filtering.
   */
  public static <A extends Annotation> boolean has(AnnotatedElement element, Class<A> annotation) {
    return get(element, annotation) != null;
  }

  /**
   * On class get the annotation - includes inheritance.
   */
  public static <A extends Annotation> A typeGet(Class<?> clazz, Class<A> annotationType) {
    while (clazz != null && clazz != Object.class) {
      final A val = clazz.getAnnotation(annotationType);
      if (val != null) {
        return val;
      }
      clazz = clazz.getSuperclass();
    }
    return null;
  }

  /**
   * On class get all the annotations - includes inheritance.
   */
  public static <A extends Annotation> Set<A> typeGetAll(Class<?> clazz, Class<A> annotationType) {
    Set<A> result = new LinkedHashSet<>();
    typeGetAllCollect(clazz, annotationType, result);
    return result;
  }

  private static <A extends Annotation> void typeGetAllCollect(Class<?> clazz, Class<A> annotationType, Set<A> result) {
    while (clazz != null && clazz != Object.class) {
      final A val = clazz.getAnnotation(annotationType);
      if (val != null) {
        result.add(val);
      }
      clazz = clazz.getSuperclass();
    }
  }

  /**
   * On class simple check for annotation - includes inheritance.
   */
  public static <A extends Annotation> boolean typeHas(Class<?> clazz, Class<A> annotation) {
    return typeGet(clazz, annotation) != null;
  }

  /**
   * Find all the annotations for the filter searching meta-annotations.
   */
  public static <A extends Annotation> Set<A> metaFindAllFor(AnnotatedElement element, Set<Class<?>> filter) {
    Set<A> visited = new HashSet<>();
    Set<A> result = new LinkedHashSet<>();
    for (Annotation ann : element.getAnnotations()) {
      metaAdd((A) ann, filter, visited, result);
    }
    return result;
  }

  private static <A extends Annotation> void metaAdd(A ann, Set<Class<?>> filter, Set<A> visited, Set<A> result) {
    if (notJavaLang(ann) && visited.add(ann)) {
      if (!filter.contains(ann.annotationType())) {
        for (Annotation metaAnn : ann.annotationType().getAnnotations()) {
          metaAdd((A) metaAnn, filter, visited, result);
        }
      }

      final Set<Annotation> repeatableAnnotations = getRepeatableAnnotations(ann);
      if (!repeatableAnnotations.isEmpty()) {
        for (Annotation repeatableAnnotation : repeatableAnnotations) {
          metaAdd((A) repeatableAnnotation, filter, visited, result);
        }
      } else {
        if (filter.contains(ann.annotationType())) {
          result.add(ann);
        }
      }
    }
  }

  private static Set<Annotation> getRepeatableAnnotations(Annotation ann) {
    try {
      Method method = ann.annotationType().getMethod("value");
      if (method.getReturnType().isArray() &&
        Annotation.class.isAssignableFrom(method.getReturnType().getComponentType())) {
        Annotation[] repeatableAnnotations = (Annotation[]) method.invoke(ann);

        return Arrays.stream(repeatableAnnotations).collect(Collectors.toSet());
      }
    } catch (NoSuchMethodException e) {
      // nop
    } catch (Exception e) {
      throw new RuntimeException("Unable to call value method on annotation: " + ann.annotationType(), e);
    }
    return Collections.emptySet();
  }

}
