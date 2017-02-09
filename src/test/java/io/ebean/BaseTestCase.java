package io.ebean;

import io.ebean.util.StringHelper;
import io.ebean.config.ServerConfig;
import io.ebean.config.TenantMode;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import org.tests.model.basic.Country;
import org.avaje.agentloader.AgentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Types;
import java.util.Arrays;


public class BaseTestCase {

  protected static Logger logger = LoggerFactory.getLogger(BaseTestCase.class);

  public static String SCHEMA_PREFIX = "";
  static {
    logger.debug("... preStart");
    if (!AgentLoader.loadAgentFromClasspath("ebean-agent", "debug=1;packages=org.tests,org.avaje.test,io.ebean")) {
      logger.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
    }
  }
  
  
  private static void initForTenancy() {
    String tenant = "CUSTOMER";
    SCHEMA_PREFIX = tenant + ".";
    
    ServerConfig config = new ServerConfig();
    config.setName("h2");
    config.loadFromProperties();
    config.loadTestProperties();
    config.setRegister(true);
    config.setDefaultServer(true);

    config.setTenantMode(TenantMode.SCHEMA);
    config.setCurrentTenantProvider(()-> {
      return tenant;
    });
    config.setTenantSchemaProvider(id -> id.toString());
    config.setAvailableTenantsProvider(conn -> Arrays.asList(tenant));
    //
    //    // When TenantMode.DB we don't really want to run DDL
    //    // and we want to explicitly specify the Database platform
    config.setDdlGenerate(true);
    config.setDdlRun(true);
    config.setDatabasePlatform(new H2Platform());
    //
    EbeanServerFactory.create(config);
  }

  /**
   * Return the generated sql trimming column alias if required.
   */
  protected String sqlOf(Query<?> query) {
    return trimSql(query.getGeneratedSql(), 0);
  }

  /**
   * Return the generated sql trimming column alias if required.
   */
  protected String sqlOf(Query<?> query, int columns) {
    return trimSql(query.getGeneratedSql(), columns);
  }

  /**
   * Trim out column alias if required from the generated sql.
   */
  protected String trimSql(String sql, int columns) {
    for (int i = 0; i <= columns; i++) {
      sql = StringHelper.replaceString(sql, " c" + i + ",", ",");
    }
    for (int i = 0; i <= columns; i++) {
      sql = StringHelper.replaceString(sql, " c" + i + " ", " ");
    }
    
    return StringHelper.replaceString(sql, "${tenant_schema}.","");
  }

  /**
   * MS SQL Server does not allow setting explicit values on identity columns
   * so tests that do this need to be skipped for SQL Server.
   */
  public boolean isSqlServer() {
    return Platform.SQLSERVER == platform();
  }

  public boolean isH2() {
    return Platform.H2 == platform();
  }

  public boolean isOracle() {
    return Platform.ORACLE == platform();
  }

  public boolean isPostgres() {
    return Platform.POSTGRES == platform();
  }

  public boolean isMySql() {
    return Platform.MYSQL == platform();
  }

  public boolean isPlatformBooleanNative() {
    return Types.BOOLEAN == spiEbeanServer().getDatabasePlatform().getBooleanDbType();
  }

  public boolean isPlatformOrderNullsSupport() {
    return isH2() || isPostgres();
  }

  /**
   * Wait for the L2 cache to propagate changes post-commit.
   */
  protected void awaitL2Cache() {
    // do nothing, used to thread sleep
  }

  protected <T> BeanDescriptor<T> getBeanDescriptor(Class<T> cls) {
    return spiEbeanServer().getBeanDescriptor(cls);
  }

  protected Platform platform() {
    return spiEbeanServer().getDatabasePlatform().getPlatform();
  }

  protected SpiEbeanServer spiEbeanServer() {
    return (SpiEbeanServer) Ebean.getDefaultServer();
  }

  protected EbeanServer server() {
    return Ebean.getDefaultServer();
  }

  protected void loadCountryCache() {

    Ebean.find(Country.class)
      .setLoadBeanCache(true)
      .findList();
  }
}
