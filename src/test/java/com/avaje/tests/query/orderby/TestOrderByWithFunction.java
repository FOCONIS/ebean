package com.avaje.tests.query.orderby;

import static org.junit.Assume.assumeFalse;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.tests.model.basic.Customer;
import com.avaje.tests.model.basic.ResetBasicData;

import org.junit.Assert;
import org.junit.Test;

public class TestOrderByWithFunction extends BaseTestCase {

  @Test
  public void testWithFunction() {

    assumeFalse("Skipping test because order by with function not yet supported for MS SQL Server.", isMsSqlServer());

    ResetBasicData.reset();

    Query<Customer> query = Ebean.find(Customer.class).order("length(name),name");

    query.findList();
    String sql = query.getGeneratedSql();

    Assert.assertTrue(sql.contains("order by length(t0.name)"));
  }
}
