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

public class TestFilterWithEnum {

  @Test
  public void test_filter_with_enum() throws InterruptedException {

    ResetBasicData.reset();

    List<Order> allOrders = Ebean.find(Order.class).findList();

    Filter<Order> filter = Ebean.filter(Order.class);
    List<Order> newOrders = filter.eq("status", Order.Status.NEW).filter(allOrders);

    Assert.assertNotNull(newOrders);
    assertThat(newOrders).hasSize(3);
  }
}
