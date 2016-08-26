package com.avaje.tests.model.basic;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.avaje.ebean.event.BeanPersistRequest;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.tests.lifecycle.SimpleContext;

import static org.junit.Assert.*;

@Entity
@Table(name = "e_basic_withlife")
public class EBasicWithLifecycle {

  @Id
  Long id;

  String name;

  @Version
  Long version;

  transient StringBuilder buffer = new StringBuilder();

  @Transient
  private String contextValue;

  @PrePersist
  public void prePersist1() {
    buffer.append("prePersist1");
  }

  @PrePersist
  public void prePersist2(BeanPersistRequest<?> o) {
    assertNotNull(o);
    buffer.append("prePersist2 " + o.getClass());
  }

  @PostPersist
  public void postPersist1() {
    buffer.append("postPersist1");
  }

  @PostPersist
  public void postPersist2(BeanPersistRequest<?> o) {
    assertNotNull(o);
    buffer.append("postPersist2 " + o.getClass());
  }

  @PreUpdate
  public void preUpdate1() {
    buffer.append("preUpdate1");
  }

  @PreUpdate
  public void preUpdate2(BeanPersistRequest<?> o) {
    assertNotNull(o);
    buffer.append("preUpdate2 " + o.getClass());
  }

  @PostUpdate
  public void postUpdate1() {
    buffer.append("postUpdate1");
  }

  @PostUpdate
  public void postUpdate2(BeanPersistRequest<?> o) {
    assertNotNull(o);
    buffer.append("postUpdate2 " + o.getClass());
  }

  @PreRemove
  public void preRemove1() {
    buffer.append("preRemove1");
  }

  @PreRemove
  public void preRemove2(BeanPersistRequest<?> o) {
    assertNotNull(o);
    buffer.append("preRemove2 " + o.getClass());
  }

  @PostRemove
  public void postRemove1() {
    buffer.append("postRemove1");
  }

  @PostRemove
  public void postRemove2(BeanPersistRequest<?> o) {
    assertNotNull(o);
    buffer.append("postRemove2 " + o.getClass());
  }

  @PostLoad
  public void postLoad1() {
    buffer.append("postLoad1");
  }

  @PostLoad
  public void postLoad2(BeanDescriptor<?> o) {
    assertNotNull(o);
    buffer.append("postLoad2 " + o.getClass());
  }

  @PostConstruct
  public void postConstruct1() {
    buffer.append("postConstruct1");
  }

  @PostConstruct
  public void postConstruct2(BeanDescriptor<?> o) {
    assertNotNull(o);
    buffer.append("postConstruct2 " + o.getClass());
    
    
    SimpleContext ctx = o.getEbeanServer().getServerConfig().getCustomContext();
    if (ctx != null) {
      contextValue = ctx.getContextValue();
    }
  }
  
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public String getBuffer() {
    return buffer.toString();
  }

  public String getContextValue() {
    return contextValue;
  }
}
