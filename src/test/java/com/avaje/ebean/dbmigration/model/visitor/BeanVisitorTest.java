package com.avaje.ebean.dbmigration.model.visitor;


import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.config.DbConstraintNaming;
import com.avaje.ebean.dbmigration.ddlgeneration.platform.DefaultConstraintMaxLength;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.deploy.BeanProperty;
import com.avaje.ebeaninternal.server.deploy.BeanPropertyAssocMany;
import com.avaje.ebeaninternal.server.deploy.BeanPropertyAssocOne;
import com.avaje.ebeaninternal.server.deploy.BeanPropertyCompound;
import com.avaje.tests.model.basic.Customer;
import com.avaje.ebean.dbmigration.model.MTable;
import com.avaje.ebean.dbmigration.model.ModelContainer;
import com.avaje.ebean.dbmigration.model.visitor.VisitAllUsing;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
/**
 * A simple test and showcase how to extend {@link BeanDescriptor BeanDescriptors} 
 * and {@link BeanProperty BeanProperties} with additional attributes.
 * 
 * <p>
 * This is very useful, if you have already similar descriptors in your application.
 * Write your own BeanVisitor, that inspects your Entites at startup and do whatever
 * you want
 * </p>
 *  
 * @author Roland Praml, FOCONIS AG
 *
 */
public class BeanVisitorTest extends BaseTestCase {

  /**
   * A simple mixin, that stores annotations as String
   * @author Roland Praml, FOCONIS AG
   */
  static class AdditionalBeanInfos {
    StringBuilder sb = new StringBuilder();
    
    public AdditionalBeanInfos(String name, Annotation... annotiations) {
      sb.append("BEAN: ").append(name).append(", ANNOTATIONS: ");
      for (Annotation ann : annotiations) {
        sb.append(' ').append(ann.toString());
      }
    }

    @Override
    public String toString() {
      return sb.toString();
    }
  }

  /**
   * A simple mixin, that stores annotations as String
   * @author Roland Praml, FOCONIS AG
   */
  static class AdditionalBeanPropertyInfos {
    AdditionalBeanInfos parent;
    StringBuilder sb = new StringBuilder();
    
    public AdditionalBeanPropertyInfos(AdditionalBeanInfos parent, String type, String name, Field field) {
      this.parent = parent;
      sb.append("TYPE: ").append(type);
      sb.append(", NAME: ").append(name);
      sb.append(", ANNOTATIONS:");
      if (field == null) {
        sb.append(" - no field present -");
      } else {
        for (Annotation ann : field.getAnnotations()) {
          sb.append(' ').append(ann.toString());
        }
      }
    }
    
    @Override
    public String toString() {
      return sb.toString();
    }
    
    public AdditionalBeanInfos getParent() {
      return parent;
    }
  }
  
  /**
   * The visitor itself, it visits all beans and can gain them with additional sugar.
   * @author Roland Praml, FOCONIS AG
   */
  static class MyBeanVisitor implements BeanVisitor {
    
    
    @Override
    public BeanPropertyVisitor visitBean(BeanDescriptor<?> descriptor) {
      AdditionalBeanInfos mixin = new AdditionalBeanInfos(descriptor.getName(), descriptor.getBeanType().getAnnotations());
      descriptor.setCustomMixin(mixin);
      return new MyBeanPropertyVisitor(mixin);
    }

  }
  /**
   * The BeanPropertyVisitor
   * @author Roland Praml, FOCONIS AG
   */
  static class MyBeanPropertyVisitor implements BeanPropertyVisitor {
    AdditionalBeanInfos parent;
    
    public MyBeanPropertyVisitor(AdditionalBeanInfos parent) {
      this.parent = parent;
    }

    @Override
    public void visitScalar(BeanProperty p) {
      p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "Scalar", p.getName(), p.getField()));
    }

    @Override
    public void visitOneImported(BeanPropertyAssocOne<?> p) {
       p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "OneImported", p.getName(), p.getField()));
    }

    @Override
    public void visitOneExported(BeanPropertyAssocOne<?> p) {
      p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "OneExported", p.getName(), p.getField()));
    }

    @Override
    public void visitMany(BeanPropertyAssocMany<?> p) {
      p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "Many", p.getName(), p.getField()));
    }

    @Override
    public void visitEnd() {
      parent = null;
    }

    @Override
    public void visitEmbeddedScalar(BeanProperty p, BeanPropertyAssocOne<?> embedded) {
      p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "EmbeddedScalar", p.getName(), p.getField()));
    }

    @Override
    public void visitEmbedded(BeanPropertyAssocOne<?> p) {
      p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "Embedded", p.getName(), p.getField()));
    }

    @Override
    public void visitCompoundScalar(BeanPropertyCompound compound, BeanProperty p) {
      p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "CompoundScalar", p.getName(), p.getField()));
    }

    @Override
    public void visitCompound(BeanPropertyCompound p) {
      p.setCustomMixin(new AdditionalBeanPropertyInfos(parent, "Compound", p.getName(), p.getField()));
    }
  };

  @Before 
  public void doTheVisit() {
    BeanVisitor visitor = new MyBeanVisitor();
    new VisitAllUsing(visitor, (SpiEbeanServer) Ebean.getDefaultServer()).visitAllBeans();
  }
  @Test
  public void test() {
    BeanDescriptor<Customer> desc = getBeanDescriptor(Customer.class);
    AdditionalBeanInfos bi = desc.getCustomMixin();
    assertThat(bi.toString()).startsWith("BEAN: Customer,");
    assertThat(bi.toString()).contains("Holds external customers"); // verify one annotation
    
    AdditionalBeanPropertyInfos bpi = desc.getBeanProperty("status").getCustomMixin();
    System.out.println(bpi.toString());
    assertThat(bpi.toString()).startsWith("TYPE: Scalar, NAME: status,");
    assertThat(bpi.toString()).contains("status of the customer");
  }
}