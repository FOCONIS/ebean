package org.tests.json;

import org.avaje.ebeantest.LoggedSqlCollector;
import org.junit.Test;
import org.tests.model.basic.EBasic;
import org.tests.model.json.JsonContainingModel;
import org.tests.model.json.JsonModel;
import org.tests.model.json.JsonModelA;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.text.json.JsonWriteOptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;


public class TestJsonEmbedded extends BaseTestCase {
  
  @Test
  public void testPlainBeanDirty() {
    
    
    EBasic basicBean = new EBasic();
    
    Ebean.save(basicBean);
        
        
    JsonContainingModel bean = new JsonContainingModel();
   
    
    JsonModel jsonBean = new JsonModelA();
    jsonBean.setName("Hello");
    jsonBean.setBasicBean(basicBean);
    
    JsonWriteOptions opts = new JsonWriteOptions();
    opts.setEntitiesAsReference(true);
    // Test JSON Roundtrip 
    String json = Ebean.json().toJson(jsonBean, opts);
    System.out.println(json);
    
    jsonBean = Ebean.json().toBean(JsonModel.class, json);
    // json contains the discriminator = type
    //assertEquals(JsonModelA.class, jsonBean.getClass());
            
    bean.setJsonModel(jsonBean);
      
    LoggedSqlCollector.start();
    Ebean.save(bean);
    assertThat(LoggedSqlCollector.stop().toString()).contains("insert into " + SCHEMA_PREFIX + "json_containing_model");
    bean = Ebean.find(JsonContainingModel.class, bean.getId());  

    assertEquals("Hello" , bean.getJsonModel().getName());
    assertEquals(JsonModelA.class, bean.getJsonModel().getClass());
    assertFalse(Ebean.getBeanState(bean).isDirty());
    
    LoggedSqlCollector.start();
    Ebean.save(bean);
    assertThat(LoggedSqlCollector.stop().toString()).isEqualTo("[]");
    
    
//    bean = Ebean.find(EBasicJsonList.class, bean.getId());  
//    
//    assertEquals("Hello" , bean.getPlainBean().getName());
//    
//    assertFalse(Ebean.getBeanState(bean).isDirty());
//    Ebean.save(bean);
//    
//    bean.getPlainBean().setName("World");
//    
//    assertFalse(Ebean.getBeanState(bean).isDirty());
//    
//    Ebean.save(bean);
//    
//    
//    bean = Ebean.find(EBasicJsonList.class, bean.getId());  
//    
//    assertEquals("World" , bean.getPlainBean().getName());
    
  }

}
