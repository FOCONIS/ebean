package com.avaje.tests.model.basic.xtra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeFalse;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.config.PersistBatch;
import org.avaje.ebeantest.LoggedSqlCollector;
import org.junit.Assume;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestInsertBatchThenUpdate extends BaseTestCase {

  @Test
  public void test() {

    assumeFalse("Skipping test because batching not yet supported for MS SQL Server.", 
        isMsSqlServer());

    LoggedSqlCollector.start();
    Transaction txn = Ebean.beginTransaction();
    try {
      txn.setBatch(PersistBatch.ALL);

      LoggedSqlCollector.start();

      EdParent parent = new EdParent();
      parent.setName("MyComputer");

      EdChild child = new EdChild();
      child.setName("Harddisk 123");
      child.setParent(parent);
      ArrayList<EdChild> children = new ArrayList<EdChild>();
      children.add(child);
      parent.setChildren(children);

      Ebean.save(parent);

      parent.setName("MyDesk");
      Ebean.save(parent);

      Ebean.commitTransaction();

      // insert statements for EdExtendedParent
      List<String> loggedSql = LoggedSqlCollector.stop();
      assertEquals(3, loggedSql.size());
      assertThat(loggedSql.get(0)).contains("insert into td_parent");
      assertThat(loggedSql.get(1)).contains("insert into td_child ");
      assertThat(loggedSql.get(2)).contains("update td_parent set parent_name=? where parent_id=?");

    } finally {
      Ebean.endTransaction();
    }
  }

}
