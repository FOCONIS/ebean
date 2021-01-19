package org.tests.model.lazywithcache;

import org.junit.Test;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Ebean;

/**
 * Test with bean cache and lazy loaded property.
 *
 * @author Noemi Szemenyei, FOCONIS AG
 *
 */

public class TestWithCacheAndLazyLoad extends BaseTestCase{

  @Test
  public void testGetters() {
    
    ChildWithCache child = new ChildWithCache();
    child.setId(1L);
    child.setName("Child With Cache");
    child.setAddress("Address");
    Ebean.save(child);
    
    ParentA parentA = new ParentA();
    parentA.setId(1L);
    parentA.setName("Parent A");
    parentA.setChild(child);
    Ebean.save(parentA);
    
    ParentB parentB = new ParentB();
    parentB.setId(1L);
    parentB.setChild(child);
    Ebean.save(parentB);
    
    DB.getDefault().getPluginApi().getServerCacheManager().clearAll();
    
    ParentA tempA = DB.find(ParentA.class, 1L);
    assert tempA != null;
    tempA.getChild().getName();
    
    ParentB tempB = DB.find(ParentB.class, 1L);
    assert tempB != null;
    
    ChildWithCache temp = tempB.getChild();
    temp.getName();
    
    String tempLazyProp = temp.getAddress();
    assert tempLazyProp != null;
    
  }

}
