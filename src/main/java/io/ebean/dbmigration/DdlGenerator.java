package io.ebean.dbmigration;

import io.ebean.Transaction;
import io.ebean.config.AvailableTenantsProvider;
import io.ebean.config.ServerConfig;
import io.ebean.config.TenantMode;
import io.ebean.config.TenantSchemaProvider;
import io.ebean.dbmigration.model.CurrentModel;
import io.ebean.util.TenantUtil;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.extraddl.model.ExtraDdlXmlReader;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebean.dbmigration.ddl.DdlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controls the generation and execution of "Create All" and "Drop All" DDL scripts.
 * <p>
 * Typically the "Create All" DDL is executed for running tests etc and has nothing to do
 * with DB Migration (diff based) DDL.
 */
public class DdlGenerator {

  private static final Logger log = LoggerFactory.getLogger(DdlGenerator.class);

  private final SpiEbeanServer server;

  private final boolean generateDdl;
  private final boolean runDdl;
  private final boolean createOnly;
  private final String sharedSchema;
  private final TenantMode tenantMode;
  private final AvailableTenantsProvider tenants;
  private final TenantSchemaProvider tenantSchemaProvider;
  
  // cache for reuse...
  private static class Cache {
    private CurrentModel currentModel;
    private String dropAllContent;
    private String createAllContent;
  }
  
  private final Map<DbSchemaType, Cache> cache = new HashMap<DbSchemaType, Cache>();

  
  
  private Cache getCache(DbSchemaType key) {
    Cache ret = cache.get(key);
    if (ret == null) {
      ret = new Cache();
      cache.put((DbSchemaType) key, ret);
    }
    return ret;
  };

  
  public DdlGenerator(SpiEbeanServer server, ServerConfig serverConfig) {
    this.server = server;
    this.generateDdl = serverConfig.isDdlGenerate();
    this.createOnly = serverConfig.isDdlCreateOnly();
    this.tenants = serverConfig.getAvailableTenantsProvider();
    this.tenantSchemaProvider = serverConfig.getTenantSchemaProvider();
    if (serverConfig.getTenantMode().isDynamicDataSource() && serverConfig.isDdlRun() && tenants == null) {
      log.warn("DDL can't be run on startup with TenantMode " + serverConfig.getTenantMode());
      this.runDdl = false;
    } else {
      this.runDdl = serverConfig.isDdlRun();
    }
    this.sharedSchema = serverConfig.getTenantSharedSchema();
    this.tenantMode = serverConfig.getTenantMode();
  }

  protected Transaction createTransaction() {
    return server.createTransaction();
  }
  
  /**
   * Generate the DDL and then run the DDL based on property settings
   * (ebean.ddl.generate and ebean.ddl.run etc).
   */
  public void execute(boolean online) {
    if (tenantMode == TenantMode.SCHEMA) {
      generateDdl(DbSchemaType.SHARED);
      generateDdl(DbSchemaType.TENANT);
    } else {
      generateDdl(DbSchemaType.ALL);
    }
    if (online && runDdl) {
      runDdl();
    }
  }

  /**
   * Generate the DDL drop and create scripts if the properties have been set.
   * @param dbSchemaType 
   */
  protected void generateDdl(DbSchemaType dbSchemaType) {
    if (generateDdl) {
      if (!createOnly) {
        writeDrop(getDropFileName(dbSchemaType), dbSchemaType);
      }
      writeCreate(getCreateFileName(dbSchemaType), dbSchemaType);
    }
  }

  /**
   * Run the DDL drop and DDL create scripts if properties have been set.
   */
  public void runDdl() {
    try {
      runInitSql();
      if (tenantMode == TenantMode.SCHEMA) {
        
        Transaction transaction = createTransaction();
        Connection connection = transaction.getConnection();
        Collection<String> tenantSchemas = new ArrayList<>();
        Set<String> existingSchemas = new HashSet<>();
        try {
          Collection<Object> tenantIds = tenants.getTenantIds(connection);
          DatabaseMetaData md = connection.getMetaData();
          ResultSet res = md.getSchemas();
          while (res.next()) {
              existingSchemas.add(res.getString(1));
          }
          for (Object tenantId : tenantIds) {
            String tenantSchema = tenantSchemaProvider.schema(tenantId);
            tenantSchemas.add(tenantSchema);
            if (!existingSchemas.contains(tenantSchema)) {
              Statement stmt = connection.createStatement();
              log.info("Creating schema {} for tenant {}", tenantSchema, tenantId);
              stmt.execute("CREATE SCHEMA " + tenantSchema);
            }
          } 
          transaction.commit();
        } catch (SQLException e) {
          throw new PersistenceException("Failed to run script", e);
        } finally {
          transaction.end();
        }
  

        for (String tenantSchema : tenantSchemas) {
          if (existingSchemas.contains(tenantSchema)) {
            runDropSql(DbSchemaType.TENANT, tenantSchema);
          }
        }
        runDropSql(DbSchemaType.SHARED, null);
        
        runCreateSql(DbSchemaType.SHARED, null);
        for (String tenantSchema : tenantSchemas) {
          runCreateSql(DbSchemaType.TENANT, tenantSchema);
        }
        runExtraDdl(DbSchemaType.SHARED, null);
        for (String tenantSchema : tenantSchemas) {
          runExtraDdl(DbSchemaType.TENANT, tenantSchema);
        }
      } else {
        runDropSql(DbSchemaType.ALL, null);
        runCreateSql(DbSchemaType.ALL, null);
        runExtraDdl(DbSchemaType.ALL, null);
      }
      runSeedSql();

    } catch (IOException e) {
      String msg = "Error reading drop/create script from file system";
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * Execute all the DDL statements in the script.
   */
  public int runScript(boolean expectErrors, String content, String scriptName, String tenantSchema) {

    DdlRunner runner = new DdlRunner(expectErrors, scriptName);

    Transaction transaction = createTransaction();
    Connection connection = transaction.getConnection();
    try {
      content = TenantUtil.applySchemas(content, sharedSchema, tenantSchema);
      
      if (expectErrors) {
        connection.setAutoCommit(true);
      }
      int count = runner.runAll(content, connection);
      if (expectErrors) {
        connection.setAutoCommit(false);
      }
      transaction.commit();
      return count;

    } catch (SQLException e) {
      throw new PersistenceException("Failed to run script", e);

    } finally {
      transaction.end();
    }
  }

  protected void runDropSql(DbSchemaType dbSchemaType, String tenantSchema) throws IOException {
    if (!createOnly) {
      if (getCache(dbSchemaType).dropAllContent == null) {
        getCache(dbSchemaType).dropAllContent = readFile(getDropFileName(dbSchemaType));
      }
      runScript(true, getCache(dbSchemaType).dropAllContent, getDropFileName(dbSchemaType), tenantSchema);
    }
  }

  protected void runCreateSql(DbSchemaType dbSchemaType, String tenantSchema) throws IOException {
    if (getCache(dbSchemaType).createAllContent == null) {
      getCache(dbSchemaType).createAllContent = readFile(getCreateFileName(dbSchemaType));
    }
    runScript(false, getCache(dbSchemaType).createAllContent, getCreateFileName(dbSchemaType), tenantSchema);
  }
  
  protected void runExtraDdl(DbSchemaType schemaType, String tenantSchema) {
    String ignoreExtraDdl = System.getProperty("ebean.ignoreExtraDdl");
    if (!"true".equalsIgnoreCase(ignoreExtraDdl)) {
      String extraApply = ExtraDdlXmlReader.buildExtra(server.getDatabasePlatform().getName(), schemaType);
      if (extraApply != null) {
        extraApply = TenantUtil.applySchemas(extraApply, sharedSchema, tenantSchema);
        runScript(false, extraApply, "extra-dll", null);
      }
    }
  }

  protected void runInitSql() throws IOException {
    runResourceScript(server.getServerConfig().getDdlInitSql());
  }

  protected void runSeedSql() throws IOException {
    runResourceScript(server.getServerConfig().getDdlSeedSql());
  }

  protected void runResourceScript(String sqlScript) throws IOException {

    if (sqlScript != null) {
      InputStream is = getClassLoader().getResourceAsStream(sqlScript);
      if (is != null) {
        String content = readContent(new InputStreamReader(is));
        runScript(false, content, sqlScript, null);
      }
    }
  }

  /**
   * Return the classLoader to use to read sql scripts as resources.
   */
  protected ClassLoader getClassLoader() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cl == null) {
      cl = this.getClassLoader();
    }
    return cl;
  }

  protected void writeDrop(String dropFile, DbSchemaType dbSchemaType) {

    try {
      writeFile(dropFile, generateDropAllDdl(dbSchemaType));
    } catch (IOException e) {
      throw new PersistenceException("Error generating Drop DDL", e);
    }
  }

  protected void writeCreate(String createFile, DbSchemaType dbSchemaType) {

    try {
      writeFile(createFile, generateCreateAllDdl(dbSchemaType));
    } catch (IOException e) {
      throw new PersistenceException("Error generating Create DDL", e);
    }
  }

  protected String generateDropAllDdl(DbSchemaType dbSchemaType) {

    try {
      return getCache(dbSchemaType).dropAllContent = currentModel(dbSchemaType).getDropAllDdl();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected String generateCreateAllDdl(DbSchemaType dbSchemaType) {

    try {
      return getCache(dbSchemaType).createAllContent = currentModel(dbSchemaType).getCreateDdl();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected String getDropFileName(DbSchemaType dbSchemaType) {
    return server.getName() + "-drop-" + dbSchemaType.name().toLowerCase() + ".sql";
  }

  protected String getCreateFileName(DbSchemaType dbSchemaType) {
    return server.getName() + "-create-" + dbSchemaType.name().toLowerCase() + ".sql";
  }

  protected CurrentModel currentModel(DbSchemaType dbSchemaType) {
    if (getCache(dbSchemaType).currentModel == null) {
      List<BeanDescriptor<?>> beanDescriptors = server.getBeanDescriptors()
       .stream()
       .filter(dbSchemaType.getFilter())
       .collect(Collectors.toList());
      getCache(dbSchemaType).currentModel = new CurrentModel(server, beanDescriptors);
    }
    return getCache(dbSchemaType).currentModel;
  }

  protected void writeFile(String fileName, String fileContent) throws IOException {

    File f = new File(fileName);

    FileWriter fw = new FileWriter(f);
    try {
      fw.write(fileContent);
      fw.flush();
    } finally {
      fw.close();
    }
  }

  protected String readFile(String fileName) throws IOException {

    File f = new File(fileName);
    if (!f.exists()) {
      return null;
    }

    return readContent(new FileReader(f));
  }

  protected String readContent(Reader reader) throws IOException {

    StringBuilder buf = new StringBuilder();

    LineNumberReader lineReader = new LineNumberReader(reader);
    try {
      String s;
      while ((s = lineReader.readLine()) != null) {
        buf.append(s).append("\n");
      }
      return buf.toString();        
      
    } finally {
      lineReader.close();
    }
  }

}
