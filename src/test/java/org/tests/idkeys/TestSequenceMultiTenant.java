/*
 * Licensed Materials - Property of FOCONIS AG
 * (C) Copyright FOCONIS AG.
 */

package org.tests.idkeys;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.junit.Test;
import org.mockito.Mockito;
import org.multitenant.partition.CurrentTenant;
import org.multitenant.partition.MtContent;
import org.multitenant.partition.MtTenant;
import org.multitenant.partition.UserContext;
import org.tests.idkeys.db.GenKeySequence;

import io.ebean.BaseTestCase;
import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.CurrentTenantProvider;
import io.ebean.config.ServerConfig;
import io.ebean.config.TenantDataSourceProvider;
import io.ebean.config.TenantMode;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;

/**
 * TODO.
 *
 * @author Marco Muehl, FOCONIS AG
 *
 */
public class TestSequenceMultiTenant {

  /**
   *  Tests using multi tenancy per database
   */
  @Test
  public void create_new_server_with_multi_tenancy_db() {

    EbeanServer db = init();
    
    UserContext.set("4711", "1");
    
    System.out.println(db.nextId(GenKeySequence.class));
    
    UserContext.set("5711", "2");
    
    System.out.println(db.nextId(GenKeySequence.class));
    
    UserContext.set("4711", "1");
    
    System.out.println(db.nextId(GenKeySequence.class));
    
//    String tenant = "customer";
//    CurrentTenantProvider tenantProvider = Mockito.mock(CurrentTenantProvider.class);
//    Mockito.doReturn(tenant).when(tenantProvider).currentId();
//
//    DataSource mockedDataSource = Mockito.mock(DataSource.class);
//    TenantDataSourceProvider dataSourceProvider = Mockito.mock(TenantDataSourceProvider.class);
//    Mockito.doReturn(mockedDataSource).when(dataSourceProvider).dataSource(tenant);
//
//    ServerConfig config = new ServerConfig();
//    config.setName("H2MultiTenant");
//    config.loadFromProperties();
//    config.setRegister(false);
//    config.setDefaultServer(false);
//
//    config.setTenantMode(TenantMode.DB);
//    config.setCurrentTenantProvider(tenantProvider);
//    config.setTenantDataSourceProvider(dataSourceProvider);
//
//    // When TenantMode.DB we don't really want to run DDL
//    // and we want to explicitly specify the Database platform
//    config.setDatabasePlatform(new PostgresPlatform());
//
//    EbeanServerFactory.create(config);
  }
  
  private static EbeanServer init() {

    ServerConfig config = new ServerConfig();

    config.setName("h2multitenantseq");
    config.loadFromProperties();
    config.setDdlGenerate(true);
    config.setDdlRun(true);
    config.setDdlExtra(false);
    config.setRegister(false);
    config.setDefaultServer(false);
    config.setCurrentTenantProvider(new CurrentTenant());
    config.setTenantMode(TenantMode.DB);
    config.setDatabasePlatform(new H2Platform());
    config.setTenantDataSourceProvider(new TenantDataSourceProvider() {
      
      Map<Object, DataSource> map = new ConcurrentHashMap<>();
      
      @Override
      public DataSource dataSource(Object tenantId) {
        if (tenantId == null) {
          tenantId = "1";
        }
        return map.computeIfAbsent(tenantId, TestSequenceMultiTenant::createDataSource);
      }
    });
    
    config.getClasses().add(GenKeySequence.class);
    

    return EbeanServerFactory.create(config);
  }
  
  private static DataSource createDataSource(Object tenantId) {
    
    ServerConfig config = new ServerConfig();

    config.setName("h2multitenantseq");
    config.loadFromProperties();
    config.setDdlGenerate(false);
    config.setDdlRun(true);
    config.setDdlExtra(false);
    config.setRegister(false);
    config.setDefaultServer(false);
    config.getDataSourceConfig().setUrl("jdbc:h2:mem:h2multitenantseq-" + tenantId);
      
    //config.getClasses().add(GenKeySequence.class);
    
    EbeanServer server = EbeanServerFactory.create(config);
    
    return server.getPluginApi().getDataSource();
  }
  
}
