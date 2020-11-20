package org.tests.update;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import org.junit.Test;

/**
 * @author Jonas P&ouml;hler, FOCONIS AG
 */
public class TestSqlUpdateBatch extends BaseTestCase {

  @Test
  public void test() {
    try (Transaction txn = DB.beginTransaction()) {
      final SqlUpdate update = DB.createSqlUpdate("update uuone set name = ? where 0=1");
      final SqlUpdate delete = DB.createSqlUpdate("delete from uuone where ?=-1");

      for (int i = 0; i < 20; i++) {
        update
          .setParameter(1, String.valueOf(i))
          .addBatch();
        delete
          .setParameter(1, String.valueOf(i))
          .addBatch();
      }
      delete.executeBatch();
      update.executeBatch();
    }
  }

}
