/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.query;

import org.junit.Assert;
import org.junit.Test;
import org.tests.model.basic.Country;
import org.tests.model.basic.Customer;
import org.tests.model.basic.ResetBasicData;

import io.ebean.BaseTestCase;
import io.ebean.DB;

/**
 * TODO.
 *
 * @author Noemi Szemenyei, FOCONIS AG
 *
 */
public class TestQueryUpdate extends BaseTestCase {
  
  @Test
  public void test() {

    ResetBasicData.reset();

    Country c = DB.find(Country.class).where().eq("code", "NZ").findOne();
    
    int count = DB.update(Customer.class)
                  .set("shippingAddress", null)
                  .where()
                  .eq("shippingAddress.country", c)
                  .update();

    Assert.assertEquals(count, 3);
  }

}
