package com.avaje.tests.rawsql;

import static org.junit.Assume.assumeFalse;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import com.avaje.tests.idkeys.db.AuditLog;

import org.junit.Test;

public class TestInsertSqlLogging extends BaseTestCase {

  @Test
  public void test() {

    assumeFalse("Skipping test because logging not yet supported for MS SQL Server.", 
        isMsSqlServer());

    Ebean.delete(AuditLog.class, 10000);

    String sql = "insert into audit_log (id, description, modified_description) values (?,?,?)";
    SqlUpdate sqlUpdate = Ebean.createSqlUpdate(sql);
    sqlUpdate.setParameter(1, 10000);
    sqlUpdate.setParameter(2, "hello");
    sqlUpdate.setParameter(3, "rob");

    sqlUpdate.execute();

  }
}
