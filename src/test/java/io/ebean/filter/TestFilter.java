/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package io.ebean.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.tests.model.basic.Order;
import org.tests.model.basic.ResetBasicData;

import io.ebean.Ebean;
import io.ebean.Filter;

public class TestFilter {

  @Test
  public void test_filter_with_enum() throws InterruptedException {

    ResetBasicData.reset();

    List<Order> allOrders = Ebean.find(Order.class).findList();

    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> newOrders = filter.eq("status", Order.Status.NEW).filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(3);
  }
  
  @Test
  public void test_is_null() throws InterruptedException {

    ResetBasicData.reset();

    List<Order> allOrders = Ebean.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> newOrders = filter.isNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(2);
  }
  
  @Test
  public void test_is_not_null() throws InterruptedException {

    ResetBasicData.reset();

    List<Order> allOrders = Ebean.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> newOrders = filter.isNotNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(3);
  }
  
  @Test
  public void test_not_is_null() throws InterruptedException {

    ResetBasicData.reset();

    List<Order> allOrders = Ebean.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> newOrders = filter.not().isNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(3);
  }
  
  @Test
  public void test_not_is_not_null() throws InterruptedException {

    ResetBasicData.reset();

    List<Order> allOrders = Ebean.find(Order.class).findList();
    allOrders.get(0).setCustomer(null);
    allOrders.get(1).setCustomer(null);
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> newOrders = filter.not().isNotNull("customer").filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(2);
  }
  

  @Test
  public void test_iContains() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Thomas Maier");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "Rob").filter(Arrays.asList(testOrder, testOrder2));
    
    assertThat(orders).hasSize(1);
  }

  
}
