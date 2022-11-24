package io.ebean.bean.extend;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for a class to extend an existing class.
 *
 * Normally, you would have annotated like the following example
 *
 * <pre>
 *   package basePkg;
 *   import extPkg.MyExtEntity;
 *   class MyBaseEntity {
 *     // this line is mandatory, to allow deletion of MyBaseEntity
 *     &#64;OneToOne(optional = true, cascade = Cascade.ALL)
 *     private MyExtEntity
 *   }
 *
 *   package extPkg;
 *   import basePkg.myBaseEntity;
 *   class MyExtEntity {
 *     &#64;OneToOne(optional = false)
 *     private MyBaseEntity
 *   }
 * </pre>
 *
 * If you spread your code over different packages or (especially in different maven modules), you'll get problems, because you'll get cyclic depencencies.
 *
 * To break up these dependencies, you can annotate 'MyExtEntity'
 *
 * <pre>
 *   package extPkg;
 *   import basePkg.myBaseEntity;
 *   &#64;VirtualEmbed(class=MyBaseEntity)
 *   class MyExtEntity {
 *     &#64;OneToOne(optional = false)
 *     private MyBaseEntity
 *   }
 * </pre>
 * This will create a virtual property in the MyBaseEntity without adding a dependency to MyExtEntity.
 * @author Alexander Wagner, FOCONIS AG
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface EntityExtension {
  Class<? extends ExtendableBean>[] value();
}
