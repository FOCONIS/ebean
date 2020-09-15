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
  public void test_istartswith() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "T").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.icontains("customerName", "Tom").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
    
    Filter<Order> filter3 = Ebean.filter(Order.class);
    List<Order> orders3 = filter3.icontains("customerName", "").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }
  
  @Test
  public void test_istartswith_with_linebreak() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "T").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.icontains("customerName", "Tom").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
    
    Filter<Order> filter3 = Ebean.filter(Order.class);
    List<Order> orders3 = filter3.icontains("customerName", "").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }
  
  @Test
  public void test_icontains() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "R").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.icontains("customerName", "Rob").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
    
    Filter<Order> filter3 = Ebean.filter(Order.class);
    List<Order> orders3 = filter3.icontains("customerName", "").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }
  
  @Test
  public void test_icontains_with_linebreak() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "R").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.icontains("customerName", "Rob").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
    
    Filter<Order> filter3 = Ebean.filter(Order.class);
    List<Order> orders3 = filter3.icontains("customerName", "").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }

  @Test
  public void test_iendswith() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "b").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.icontains("customerName", "Rob").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
    
    Filter<Order> filter3 = Ebean.filter(Order.class);
    List<Order> orders3 = filter3.icontains("customerName", "").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }
  
  @Test
  public void test_iendswith_with_linebreak() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.icontains("customerName", "b").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.icontains("customerName", "Rob").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
    
    Filter<Order> filter3 = Ebean.filter(Order.class);
    List<Order> orders3 = filter3.icontains("customerName", "").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders3).hasSize(2);
  }
  
  @Test
  public void test_ilike() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.ilike("customerName", "%R%").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.ilike("customerName", "%Rob%").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
  }
  
  @Test
  public void test_ilike_with_linebreak() {
    ResetBasicData.reset();
    
    Order testOrder = new Order();
    testOrder.setCustomerName("Tom \n Werb");
    
    Order testOrder2 = new Order();
    testOrder2.setCustomerName("Thomas \n Rob");
    
    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> orders = filter.ilike("customerName", "%R%").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders).hasSize(2);
    
    Filter<Order> filter2 = Ebean.filter(Order.class);
    List<Order> orders2 = filter2.ilike("customerName", "%Rob%").filter(Arrays.asList(testOrder, testOrder2));
    assertThat(orders2).hasSize(1);
  }
}
