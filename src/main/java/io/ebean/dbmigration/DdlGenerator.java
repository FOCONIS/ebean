package io.ebean.dbmigration;

import io.ebean.Transaction;
import io.ebean.config.AvailableTenantsProvider;
import io.ebean.config.ServerConfig;
import io.ebean.config.TenantSchemaProvider;
import io.ebean.dbmigration.model.CurrentModel;
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
import java.sql.SQLException;
import java.util.List;
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
  private final String sharedschema;
  private final AvailableTenantsProvider tenants;
  private final TenantSchemaProvider tenantSchemaProvider;
  
  private CurrentModel currentModel;
  private TenantBeanType currentModelTenantBeanType;
  private String dropAllContent;
  private String createAllContent;





  public DdlGenerator(SpiEbeanServer server, ServerConfig serverConfig, String sharedschema) {
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
    this.sharedschema = sharedschema;
  }

  /**
   * Generate the DDL and then run the DDL based on property settings
   * (ebean.ddl.generate and ebean.ddl.run etc).
   */
  public void execute(boolean online) {
    if (sharedschema != null) {
      generateDdl(TenantBeanType.SHARED);
      generateDdl(TenantBeanType.TENANT);
    } else {
      generateDdl(TenantBeanType.ALL);
    }
    if (online && runDdl) {
      runDdl();
    }
  }

  /**
   * Generate the DDL drop and create scripts if the properties have been set.
   * @param tenantBeanType 
   */
  protected void generateDdl(TenantBeanType tenantBeanType) {
    if (generateDdl) {
      if (!createOnly) {
        writeDrop(getDropFileName(tenantBeanType), tenantBeanType);
      }
      writeCreate(getCreateFileName(tenantBeanType), tenantBeanType);
    }
  }

  /**
   * Run the DDL drop and DDL create scripts if properties have been set.
   */
  public void runDdl() {
    try {
      runInitSql(sharedschema);
      if (sharedschema != null) {
        
        
        Transaction transaction = server.createTransaction();
        Connection connection = transaction.getConnection();
        List<Object> tenantIds = null;
        try {
          tenantIds = tenants.getTenantIds(connection);
        } catch (SQLException e) {
          throw new PersistenceException("Failed to run script", e);
        } finally {
          transaction.end();
        }
        for (Object tenantId : tenantIds) {
          String schema = tenantSchemaProvider.schema(tenantId);
          runDropSql(TenantBeanType.SHARED, schema);
        }
        runDropSql(TenantBeanType.SHARED, sharedschema);
        runCreateSql(TenantBeanType.SHARED, sharedschema);
        for (Object tenantId : tenantIds) {
          String schema = tenantSchemaProvider.schema(tenantId);
          runCreateSql(TenantBeanType.SHARED, schema);
        }
      } else {
        runDropSql(TenantBeanType.ALL, null);
        runCreateSql(TenantBeanType.ALL, null);
      }
      runSeedSql(sharedschema);

    } catch (IOException e) {
      String msg = "Error reading drop/create script from file system";
      throw new RuntimeException(msg, e);
    }
  }

  /**
   * Execute all the DDL statements in the script.
   */
  public int runScript(boolean expectErrors, String content, String scriptName, String schema) {

    DdlRunner runner = new DdlRunner(expectErrors, scriptName);

    Transaction transaction = server.createTransaction();
    Connection connection = transaction.getConnection();
    try {
      if (schema != null) {
        connection.setSchema(schema);
      }
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

  protected void runDropSql(TenantBeanType tenantBeanType, String schema) throws IOException {
    if (!createOnly) {
      if (dropAllContent == null) {
        dropAllContent = readFile(getDropFileName(tenantBeanType));
      }
      runScript(true, dropAllContent, getDropFileName(tenantBeanType), schema);
    }
  }

  protected void runCreateSql(TenantBeanType tenantBeanType, String schema) throws IOException {
    if (createAllContent == null) {
      createAllContent = readFile(getCreateFileName(tenantBeanType));
    }
    runScript(false, createAllContent, getCreateFileName(tenantBeanType), schema);

    String ignoreExtraDdl = System.getProperty("ebean.ignoreExtraDdl");
    if (!"true".equalsIgnoreCase(ignoreExtraDdl)) {
      String extraApply = ExtraDdlXmlReader.buildExtra(server.getDatabasePlatform().getName());
      if (extraApply != null) {
        runScript(false, extraApply, "extra-dll", schema);
      }
    }
  }

  protected void runInitSql(String schema) throws IOException {
    runResourceScript(server.getServerConfig().getDdlInitSql(), schema);
  }

  protected void runSeedSql(String schema) throws IOException {
    runResourceScript(server.getServerConfig().getDdlSeedSql(), schema);
  }

  protected void runResourceScript(String sqlScript, String schema) throws IOException {

    if (sqlScript != null) {
      InputStream is = getClassLoader().getResourceAsStream(sqlScript);
      if (is != null) {
        String content = readContent(new InputStreamReader(is));
        runScript(false, content, sqlScript, schema);
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

  protected void writeDrop(String dropFile, TenantBeanType tenantBeanType) {

    try {
      writeFile(dropFile, generateDropAllDdl(tenantBeanType));
    } catch (IOException e) {
      throw new PersistenceException("Error generating Drop DDL", e);
    }
  }

  protected void writeCreate(String createFile, TenantBeanType tenantBeanType) {

    try {
      writeFile(createFile, generateCreateAllDdl(tenantBeanType));
    } catch (IOException e) {
      throw new PersistenceException("Error generating Create DDL", e);
    }
  }

  protected String generateDropAllDdl(TenantBeanType tenantBeanType) {

    try {
      dropAllContent = currentModel(tenantBeanType).getDropAllDdl();
      return dropAllContent;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected String generateCreateAllDdl(TenantBeanType tenantBeanType) {

    try {
      createAllContent = currentModel(tenantBeanType).getCreateDdl();
      return createAllContent;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected String getDropFileName(TenantBeanType tenantBeanType) {
    return server.getName() + "-drop-" + tenantBeanType.name().toLowerCase() + ".sql";
  }

  protected String getCreateFileName(TenantBeanType tenantBeanType) {
    return server.getName() + "-create-" + tenantBeanType.name().toLowerCase() + ".sql";
  }

  protected CurrentModel currentModel(TenantBeanType tenantBeanType) {
    if (currentModel == null || currentModelTenantBeanType != tenantBeanType) {
      List<BeanDescriptor<?>> beanDescriptors = server.getBeanDescriptors()
       .stream()
       .filter(tenantBeanType.getFilter())
       .collect(Collectors.toList());
      currentModel = new CurrentModel(server, beanDescriptors);
      currentModelTenantBeanType = tenantBeanType;
    }
    return currentModel;
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
