package org.tests.it.ddlgeneration;


import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.Platform;
import io.ebean.config.PropertyMap;
import io.ebean.config.ServerConfig;
import io.ebean.config.TenantMode;
import io.ebean.dbmigration.DbMigration;

@FixMethodOrder
public class TestDdlGeneration {

  private static String[] currentTenant = new String[1];

  
  @BeforeClass
  public static void setup() throws IOException {
    ServerConfig config = new ServerConfig();
    config.loadFromProperties();
    config.setCurrentUserProvider(() -> "default"); 
    config.setDefaultServer(true);
    config.setRegister(true);

    config.setTenantMode(TenantMode.SCHEMA);
    config.setCurrentTenantProvider(() -> currentTenant[0]);
    EbeanServerFactory.create(config);

    DbMigration dbMigration = new DbMigration();
    dbMigration.addPlatform(Platform.POSTGRES, "postgres");
    dbMigration.addPlatform(Platform.SQLSERVER, "sqlserver");
    dbMigration.generateMigration();
    
  }

  protected EbeanServer server() {
    return Ebean.getDefaultServer();
  }

  @Test
  public void test02createTenant1Data() throws IOException {
    currentTenant[0] = "1";
    assertEquals("1", Ebean.getPluginApi().getServerConfig().getCurrentTenantProvider().currentId());

    GlobalTestModel gm1 = new GlobalTestModel();
    gm1.setName("foo");
    server().save(gm1);
    
    GlobalTestModel gm2 = new GlobalTestModel();
    gm1.setName("bar");
    server().save(gm2);
    
    LocalTestModel lm1 = new LocalTestModel();
    lm1.setName("lm1");
    LocalTestModel lm2 = new LocalTestModel();
    lm2.setName("lm2");
    LocalTestModel lm3 = new LocalTestModel();
    lm3.setName("lm3");
    
    gm1.setLocalTestModels(Arrays.asList(lm1, lm2, lm3));
    
  }
}
