package org.tests.it.ddlgeneration;


import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class TestDdlGeneration {

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
  public void test01createDbMigrationScript() throws IOException {
    DbMigration dbMigration = new DbMigration();
    dbMigration.addPlatform(Platform.POSTGRES, "postgres");
    dbMigration.addPlatform(Platform.SQLSERVER, "sqlserver");
    dbMigration.addPlatform(Platform.H2, "h2");
    dbMigration.generateMigration();
  }


  @Test
  public void test02createTenant1Data() throws IOException, SQLException {
    currentTenant[0] = "1";
    assertEquals("1", Ebean.getPluginApi().getServerConfig().getCurrentTenantProvider().currentId());

    GlobalTestModel gm1 = new GlobalTestModel();
    gm1.setName("foo");


    GlobalTestModel gm2 = new GlobalTestModel();
    gm1.setName("bar");
    server().save(gm2);

    LocalTestModel lm1 = new LocalTestModel();
    lm1.setName("tenant 1");
    server().save(lm1);
    LocalTestModel lm2 = new LocalTestModel();
    lm2.setName("lm2");
    LocalTestModel lm3 = new LocalTestModel();
    lm3.setName("lm3");

    gm1.setLocalTestModels(Arrays.asList(lm1, lm2, lm3));
    server().save(gm1);

    GlobalCachable gc = new GlobalCachable();
    gc.setName("GlobalCachable");
    server().save(gc);
    
    LocalCachable lc = new LocalCachable();
    lc.setName("LocalCachable Tenant 1");
    server().save(lc);
  }

  @Test
  public void test03createTenant2Data() throws IOException, SQLException {
    currentTenant[0] = "2";
    assertEquals("2", Ebean.getPluginApi().getServerConfig().getCurrentTenantProvider().currentId());

    GlobalTestModel gm3 = new GlobalTestModel();
    gm3.setName("baz");
    server().save(gm3);


    LocalTestModel lm4 = new LocalTestModel();
    lm4.setName("tenant 2b");
    server().save(lm4);
    LocalTestModel lm5 = new LocalTestModel();
    lm5.setName("lm5");

    gm3.setLocalTestModels(Arrays.asList(lm4, lm5));
    server().save(gm3);

    assertTrue(Ebean.find(LocalCachable.class).findList().isEmpty());
    
    LocalCachable lc = new LocalCachable();
    lc.setName("LocalCachable Tenant 2");
    server().save(lc);
    
    // Dump DB for debug    
    //    Transaction txn = Ebean.beginTransaction();
    //    try {
    //      Statement stmt = txn.getConnection().createStatement();
    //      // See: https://groups.google.com/forum/#!topic/h2-database/eJGE-CD_qhk
    //      stmt.executeQuery("SCRIPT TO 'dump.sql'"); // will produce wrong results if H2 QUERY_CACHE_SIZE!=0  
    //    } finally {
    //      Ebean.endTransaction();
    //    }

  }

  @Test
  public void test04readTenant1Data() throws IOException {
    currentTenant[0] = "1";
    
    assertEquals(3, Ebean.find(GlobalTestModel.class).findCount()); // gm1/gm2/gm3
    assertEquals(3, Ebean.find(LocalTestModel.class).findCount()); // lm1/lm2/lm3
    
    GlobalCachable cache1 = Ebean.find(GlobalCachable.class).where().idEq(1).findUnique();
    assertEquals("GlobalCachable", cache1.getName());
    
    LocalCachable cache2 = Ebean.find(LocalCachable.class).where().idEq(1).findUnique();
    assertEquals("LocalCachable Tenant 1", cache2.getName());
  }

  @Test
  public void test05readTenant2Data() throws IOException, InterruptedException {
    currentTenant[0] = "2";
    assertEquals(3, Ebean.find(GlobalTestModel.class).findCount()); // gm1/gm2/gm3
    assertEquals(2, Ebean.find(LocalTestModel.class).findCount()); // lm4/lm5 - will fail if datasource.db.pstmtCacheSize!=0
    
    
    GlobalCachable cache1 = Ebean.find(GlobalCachable.class).where().idEq(1).findUnique();
    assertEquals("GlobalCachable", cache1.getName());
    
    LocalCachable cache2 = Ebean.find(LocalCachable.class).where().idEq(1).findUnique();
    assertEquals("LocalCachable Tenant 2", cache2.getName());
  }
    
    
  @Test
  public void test06readTenant1and2Data() throws IOException {
    currentTenant[0] = "1";
    
    assertEquals(3, Ebean.find(GlobalTestModel.class).findCount()); // gm1/gm2/gm3
    assertEquals(3, Ebean.find(LocalTestModel.class).findCount()); // lm1/lm2/lm3
    
    GlobalCachable cache1 = Ebean.find(GlobalCachable.class).where().idEq(1).findUnique();
    assertEquals("GlobalCachable", cache1.getName());
    
    LocalCachable cache2 = Ebean.find(LocalCachable.class).where().idEq(1).findUnique();
    assertEquals("LocalCachable Tenant 1", cache2.getName());

    
    currentTenant[0] = "2";
    assertEquals(3, Ebean.find(GlobalTestModel.class).findCount()); // gm1/gm2/gm3
    assertEquals(2, Ebean.find(LocalTestModel.class).findCount()); // lm4/lm5 - will fail if datasource.db.pstmtCacheSize!=0
    
    
    cache1 = Ebean.find(GlobalCachable.class).where().idEq(1).findUnique();
    assertEquals("GlobalCachable", cache1.getName());
    
    cache2 = Ebean.find(LocalCachable.class).where().idEq(1).findUnique();
    assertEquals("LocalCachable Tenant 2", cache2.getName());

  }
  
  @Test
  public void test07checkDataSource() throws IOException, SQLException {
    currentTenant[0] = "1";
    DataSource ds = Ebean.getPluginApi().getDataSource();
    Connection conn = ds.getConnection();
    conn.setSchema("TENANT_1");
    PreparedStatement ps1 = conn.prepareStatement("SELECT * from local_model");
    ps1.close();
    PreparedStatement ps2 = conn.prepareStatement("SELECT * from local_model");
    ps2.close();
    
    conn.setSchema("TENANT_2");
    PreparedStatement ps3 = conn.prepareStatement("SELECT * from local_model");
    ps3.close();
    
    
    assertTrue(ps1 == ps2);  // test if pstmtCache is working
    assertFalse(ps1 == ps3); // test if datasource recognize schema change

  }

}
