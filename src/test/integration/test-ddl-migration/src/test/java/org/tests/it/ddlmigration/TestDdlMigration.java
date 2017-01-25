package org.tests.it.ddlmigration;


import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.Platform;
import io.ebean.Transaction;
import io.ebean.config.ServerConfig;
import io.ebean.config.TenantMode;
import io.ebean.dbmigration.DbMigration;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDdlMigration {

  private static String[] currentTenant = new String[1];


  @BeforeClass
  public static void setup() throws IOException {
    //DriverManager.setLogStream(System.err);
    ServerConfig config = new ServerConfig();
    config.loadFromProperties();
    config.setCurrentUserProvider(() -> "default"); 
    config.setDefaultServer(true);
    config.setRegister(true);

    config.setTenantMode(TenantMode.SCHEMA);
    config.setCurrentTenantProvider(() -> currentTenant[0]);
    EbeanServerFactory.create(config);

  }

  protected EbeanServer server() {
    return Ebean.getDefaultServer();
  }

  @Test
  public void test01createDbMigrationScript() throws IOException, SQLException {
    Transaction txn = server().beginTransaction();
    try {
      Statement stmt = txn.getConnection().createStatement();
      //      // See: https://groups.google.com/forum/#!topic/h2-database/eJGE-CD_qhk
      stmt.executeQuery("SCRIPT TO 'dump-create-all.sql'"); // will produce wrong results if H2 QUERY_CACHE_SIZE!=0  
      
    } finally {
      server().endTransaction();
    }
  }


}
