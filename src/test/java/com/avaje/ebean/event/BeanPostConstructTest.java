package com.avaje.ebean.event;


import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.BeanState;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.tests.model.basic.EBasicVer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanPostConstructTest extends BaseTestCase {

  PostConstruct postConstruct = new PostConstruct(false);

  @Test
  public void testPostLoad() {

    EbeanServer ebeanServer = getEbeanServer();

    EBasicVer bean = new EBasicVer("testPostConstruct");
    bean.setDescription("someDescription");
    bean.setOther("other");

    ebeanServer.save(bean);

    EBasicVer found = ebeanServer.find(EBasicVer.class)
        .select("name, other")
        .setId(bean.getId())
        .findUnique();

    assertThat(postConstruct.methodsCalled).hasSize(1);
    assertThat(postConstruct.methodsCalled).containsExactly("postConstruct");
    assertThat(postConstruct.beanState.getLoadedProps()).containsExactly("id", "name", "other");
    assertThat(postConstruct.bean).isSameAs(found);

    ebeanServer.delete(bean);
  }


  private EbeanServer getEbeanServer() {

    System.setProperty("ebean.ignoreExtraDdl", "true");

    ServerConfig config = new ServerConfig();

    config.setName("h2ebasicver");
    config.loadFromProperties();
    config.setDdlGenerate(true);
    config.setDdlRun(true);

    config.setRegister(false);
    config.setDefaultServer(false);
    config.getClasses().add(EBasicVer.class);

    config.add(postConstruct);
    
    return EbeanServerFactory.create(config);
  }

  static class PostConstruct implements BeanPostConstruct {


    boolean dummy;

    List<String> methodsCalled = new ArrayList<String>();

    Object bean;

    BeanState beanState;

    /**
     * No default constructor so only registered manually.
     */
    PostConstruct(boolean dummy) {
      this.dummy = dummy;
    }

    @Override
    public boolean isRegisterFor(Class<?> cls) {
      return true;
    }

    @Override
    public void postConstruct(Object bean) {
      this.methodsCalled.add("postConstruct");
      this.bean = bean;
      this.beanState = Ebean.getBeanState(bean);
    }

  }

}