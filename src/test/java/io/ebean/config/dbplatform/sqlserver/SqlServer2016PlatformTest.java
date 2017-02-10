package io.ebean.config.dbplatform.sqlserver;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Vilmos Nagy  <vilmos.nagy@outlook.com>
 */
public class SqlServer2016PlatformTest {

  @Test
  public void testHistorySupport() {
    SqlServerPlatform.setVersion(SqlServerPlatform.SQLSERVER_2016);
    SqlServerPlatform platform = SqlServerPlatform.create();
    assertTrue(platform.getHistorySupport() instanceof SqlServer2016HistorySupport);
  }
}
