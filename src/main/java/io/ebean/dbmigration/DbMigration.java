package io.ebean.dbmigration;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.config.DbConstraintNaming;
import io.ebean.config.DbMigrationConfig;
import io.ebean.Platform;
import io.ebean.config.ServerConfig;
import io.ebean.config.dbplatform.db2.DB2Platform;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.config.dbplatform.h2.H2Platform;
import io.ebean.config.dbplatform.sqlserver.SqlServerPlatform;
import io.ebean.config.dbplatform.mysql.MySqlPlatform;
import io.ebean.config.dbplatform.oracle.OraclePlatform;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;
import io.ebean.config.dbplatform.sqlite.SQLitePlatform;
import io.ebean.dbmigration.ddlgeneration.DdlWrite;
import io.ebean.dbmigration.migration.Migration;
import io.ebean.dbmigration.migrationreader.MigrationXmlWriter;
import io.ebean.dbmigration.model.CurrentModel;
import io.ebean.dbmigration.model.MConfiguration;
import io.ebean.dbmigration.model.MigrationModel;
import io.ebean.dbmigration.model.MigrationVersion;
import io.ebean.dbmigration.model.ModelContainer;
import io.ebean.dbmigration.model.ModelDiff;
import io.ebean.dbmigration.model.PlatformDdlWriter;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.extraddl.model.DdlScript;
import io.ebeaninternal.extraddl.model.ExtraDdl;
import io.ebeaninternal.extraddl.model.ExtraDdlXmlReader;
import io.ebeaninternal.server.deploy.BeanDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates DB Migration xml and sql scripts.
 * <p>
 * Reads the prior migrations and compares with the current model of the EbeanServer
 * and generates a migration 'diff' in the form of xml document with the logical schema
 * changes and a series of sql scripts to apply, rollback the applied changes if necessary
 * and drop objects (drop tables, drop columns).
 * </p>
 * <p>
 * This does not run the migration or ddl scripts but just generates them.
 * </p>
 * <pre>{@code
 *
 *       DbMigration migration = new DbMigration();
 *       migration.setPathToResources("src/main/resources");
 *       migration.setPlatform(DbPlatformName.ORACLE);
 *
 *       migration.generateMigration();
 *
 * }</pre>
 */
public class DbMigration {

  protected static final Logger logger = LoggerFactory.getLogger(DbMigration.class);

  private static final String initialVersion = "1.0";

  private static final String GENERATED_COMMENT = "THIS IS A GENERATED FILE - DO NOT MODIFY";

  /**
   * Set to true if DbMigration run with online EbeanServer instance.
   */
  protected final boolean online;

  protected SpiEbeanServer server;

  protected DbMigrationConfig migrationConfig;

  protected String pathToResources = "src/main/resources";

  protected DatabasePlatform databasePlatform;

  protected List<Pair> platforms = new ArrayList<>();

  protected ServerConfig serverConfig;

  protected DbConstraintNaming constraintNaming;


  
  /**
   * Create for offline migration generation.
   */
  public DbMigration() {
    this.online = false;
  }

  /**
   * Create using online EbeanServer.
   */
  public DbMigration(EbeanServer server) {
    this.online = true;
    setServer(server);
  }

  /**
   * Set the path from the current working directory to the application resources.
   * <p>
   * This defaults to maven style 'src/main/resources'.
   */
  public void setPathToResources(String pathToResources) {
    this.pathToResources = pathToResources;
  }

  /**
   * Set the server to use to determine the current model.
   * Typically this is not called explicitly.
   */
  public void setServer(EbeanServer ebeanServer) {
    this.server = (SpiEbeanServer) ebeanServer;
    setServerConfig(server.getServerConfig());
  }

  /**
   * Set the serverConfig to use. Typically this is not called explicitly.
   */
  public void setServerConfig(ServerConfig config) {
    if (this.serverConfig == null) {
      this.serverConfig = config;
    }
    if (migrationConfig == null) {
      this.migrationConfig = serverConfig.getMigrationConfig();
    }
    if (constraintNaming == null) {
      this.constraintNaming = serverConfig.getConstraintNaming();
    }
  }

  /**
   * Set the specific platform to generate DDL for.
   * <p>
   * If not set this defaults to the platform of the default server.
   * </p>
   */
  public void setPlatform(Platform platform) {
    setPlatform(getPlatform(platform));
  }

  /**
   * Set the specific platform to generate DDL for.
   * <p>
   * If not set this defaults to the platform of the default server.
   * </p>
   */
  public void setPlatform(DatabasePlatform databasePlatform) {
    this.databasePlatform = databasePlatform;
    if (!online) {
      DbOffline.setPlatform(databasePlatform.getPlatform());
    }
  }

  /**
   * Add an additional platform to write the migration DDL.
   * <p>
   * Use this when you want to generate sql scripts for multiple database platforms
   * from the migration (e.g. generate migration sql for MySql, Postgres and Oracle).
   * </p>
   * @param filter 
   */
  public void addPlatform(Platform platform, String prefix) {
    platforms.add(new Pair(getPlatform(platform), prefix));
  }
 
  /**
   * Generate the next migration xml file and associated apply and rollback sql scripts.
   * <p>
   * This does not run the migration or ddl scripts but just generates them.
   * </p>
   * <h3>Example: Run for a single specific platform</h3>
   * <pre>{@code
   *
   *       DbMigration migration = new DbMigration();
   *       migration.setPathToResources("src/main/resources");
   *       migration.setPlatform(DbPlatformName.ORACLE);
   *
   *       migration.generateMigration();
   *
   * }</pre>
   * <p>
   * <h3>Example: Run migration generating DDL for multiple platforms</h3>
   * <pre>{@code
   *
   *       DbMigration migration = new DbMigration();
   *       migration.setPathToResources("src/main/resources");
   *
   *       migration.addPlatform(DbPlatformName.POSTGRES, "pg");
   *       migration.addPlatform(DbPlatformName.MYSQL, "mysql");
   *       migration.addPlatform(DbPlatformName.ORACLE, "mysql");
   *
   *       migration.generateMigration();
   *
   * }</pre>
   */
  public void generateMigration() throws IOException {

    // use this flag to stop other plugins like full DDL generation
    if (!online) {
      DbOffline.setGenerateMigration();
      if (databasePlatform == null && !platforms.isEmpty()) {
        // for multiple platform generation set the general platform
        // to H2 so that it runs offline without DB connection
        setPlatform(platforms.get(0).platform);
      }
    }
    setDefaults();
    try {
      for (Request request : createRequests()) {
        if (platforms.isEmpty()) {
          generateExtraDdl(request.migrationDir, databasePlatform);
        }
  
        String pendingVersion = generatePendingDrop();
        if (pendingVersion != null) {
          generatePendingDrop(request, pendingVersion);
        } else {
          generateDiff(request);
        }
      }
    } finally {
      if (!online) {
        DbOffline.reset();
      }
    }
  }

  /**
   * Generate "repeatable" migration scripts.
   * <p>
   * These take scrips from extra-dll.xml (typically views) and outputs "repeatable"
   * migration scripts (starting with "R__") to be run by FlywayDb or Ebean's own
   * migration runner.
   * </p>
   */
  private void generateExtraDdl(File migrationDir, DatabasePlatform dbPlatform) throws IOException {

    if (dbPlatform != null) {
      ExtraDdl extraDdl = ExtraDdlXmlReader.read("/extra-ddl.xml");
      if (extraDdl != null) {
        List<DdlScript> ddlScript = extraDdl.getDdlScript();
        for (DdlScript script : ddlScript) {
          if (ExtraDdlXmlReader.matchPlatform(dbPlatform.getName(), script.getPlatforms())) {
            writeExtraDdl(migrationDir, script);
          }
        }
      }
    }
  }

  /**
   * Write (or override) the "repeatable" migration script.
   */
  private void writeExtraDdl(File migrationDir, DdlScript script) throws IOException {

    String fullName = repeatableMigrationName(script.getName());

    logger.info("writing repeatable script {}", fullName);

    File file = new File(migrationDir, fullName);
    FileWriter writer = new FileWriter(file);
    writer.write(script.getValue());
    writer.flush();
    writer.close();
  }

  private String repeatableMigrationName(String scriptName) {
    return "R__" + scriptName.replace(' ', '_') + migrationConfig.getApplySuffix();
  }

  /**
   * Generate the diff migration.
   */
  private void generateDiff(Request request) throws IOException {

    List<String> pendingDrops = request.getPendingDrops();
    if (!pendingDrops.isEmpty()) {
      logger.info("Pending un-applied drops in versions {}, tenant: {}", pendingDrops, request.tenantBeanType);
    }

    Migration migration = request.createDiffMigration();
    if (migration == null) {
      logger.info("no changes detected - no migration written. Tenant: {}", request.tenantBeanType);
    } else {
      // there were actually changes to write
      generateMigration(request, migration, null);
    }
  }

  /**
   * Generate the migration based on the pendingDrops from a prior version.
   */
  private void generatePendingDrop(Request request, String pendingVersion) throws IOException {

    Migration migration = request.migrationForPendingDrop(pendingVersion);

    generateMigration(request, migration, pendingVersion);

    List<String> pendingDrops = request.getPendingDrops();
    if (!pendingDrops.isEmpty()) {
      logger.info("... remaining pending un-applied drops in versions {}", pendingDrops);
    }
  }

  private List<Request> createRequests() {
    if (serverConfig.getTenantSharedSchema() != null && !serverConfig.getTenantSharedSchema().isEmpty()) {
      return Arrays.asList(new Request(TenantBeanType.SHARED), new Request(TenantBeanType.TENANT));
    } else {
      return Arrays.asList(new Request(TenantBeanType.ALL));
    }
  }

  private class Request {

    final File migrationDir;
    final File modelDir;
    final MigrationModel migrationModel;
    final CurrentModel currentModel;
    final ModelContainer migrated;
    final ModelContainer current;
    final TenantBeanType tenantBeanType;

    /**
     * Create a request. SharedOnly is a tri-state boolean:
     * <br>
     * null: means no shared/non shared support<br>
     * true/false: generate shared/tenant scripts
     * @param sharedOnly
     */
    private Request(TenantBeanType tenantBeanType) {
      
      this.migrationDir = getMigrationDirectory();
      this.tenantBeanType = tenantBeanType;
      this.modelDir = getModelDirectory(migrationDir, tenantBeanType);
      this.migrationModel = new MigrationModel(modelDir, migrationConfig.getModelSuffix());
      this.migrated = migrationModel.read();
      List<BeanDescriptor<?>> beanDescriptors = server.getBeanDescriptors()
          .stream()
          .filter(tenantBeanType.getFilter())
          .collect(Collectors.toList());
    
      this.currentModel = new CurrentModel(server, constraintNaming, beanDescriptors);
      this.current = currentModel.read();
    }

    /**
     * Return the migration for the pending drops for a given version.
     */
    public Migration migrationForPendingDrop(String pendingVersion) {

      Migration migration = migrated.migrationForPendingDrop(pendingVersion);

      // register any remaining pending drops
      migrated.registerPendingHistoryDropColumns(current);
      return migration;
    }

    /**
     * Return the list of versions that have pending un-applied drops.
     */
    public List<String> getPendingDrops() {
      return migrated.getPendingDrops();
    }

    /**
     * Create and return the diff of the current model to the migration model.
     */
    public Migration createDiffMigration() {
      ModelDiff diff = new ModelDiff(migrated);
      diff.compareTo(current);
      return diff.isEmpty() ? null : diff.getMigration();
    }
  }

  private void generateMigration(Request request, Migration dbMigration, String dropsFor) throws IOException {

    String fullVersion = getFullVersion(request.migrationModel, dropsFor);

    logger.info("generating migration: {}, TenantMode: {}", fullVersion, request.tenantBeanType);
    if (!writeMigrationXml(dbMigration, request.modelDir, fullVersion)) {
      logger.warn("migration already exists, not generating DDL");

    } else {
      if (!platforms.isEmpty()) {
        writeExtraPlatformDdl(fullVersion, request.currentModel, dbMigration, request.migrationDir, request.tenantBeanType);

      } else if (databasePlatform != null) {
        // writer needs the current model to provide table/column details for
        // history ddl generation (triggers, history tables etc)
        DdlWrite write = new DdlWrite(new MConfiguration(), request.current);
        PlatformDdlWriter writer = createDdlWriter(databasePlatform, "");
        writer.processMigration(dbMigration, write, request.migrationDir, fullVersion);
      }
    }
  }

  /**
   * Return true if the next pending drop changeSet should be generated as the next migration.
   */
  private String generatePendingDrop() {

    String nextDrop = System.getProperty("ddl.migration.pendingDropsFor");
    if (nextDrop != null) {
      return nextDrop;
    }
    return migrationConfig.getGeneratePendingDrop();
  }

  /**
   * Return the full version for the migration being generated.
   * <p>
   * The full version can contain a comment suffix after a "__" double underscore.
   */
  private String getFullVersion(MigrationModel migrationModel, String dropsFor) {

    String version = migrationConfig.getVersion();
    if (version == null) {
      version = migrationModel.getNextVersion(initialVersion);
    }

    String fullVersion = migrationConfig.getApplyPrefix() + version;
    if (migrationConfig.getName() != null) {
      fullVersion += "__" + toUnderScore(migrationConfig.getName());

    } else if (dropsFor != null) {
      fullVersion += "__" + toUnderScore("dropsFor_" + MigrationVersion.trim(dropsFor));

    } else if (version.equals(initialVersion)) {
      fullVersion += "__initial";
    }
    return fullVersion;
  }

  /**
   * Replace spaces with underscores.
   */
  private String toUnderScore(String name) {
    return name.replace(' ', '_');
  }

  /**
   * Write any extra platform ddl.
   */
  protected void writeExtraPlatformDdl(String fullVersion, CurrentModel currentModel, Migration dbMigration, 
      File writePath, TenantBeanType tennantBeanType) throws IOException {

    for (Pair pair : platforms) {
      DdlWrite platformBuffer = new DdlWrite(new MConfiguration(), currentModel.read());
      PlatformDdlWriter platformWriter = createDdlWriter(pair);
      File subPath = platformWriter.subPath(writePath, pair.prefix, tennantBeanType);
      platformWriter.processMigration(dbMigration, platformBuffer, subPath, fullVersion);

      generateExtraDdl(subPath, pair.platform);
    }
  }

  private PlatformDdlWriter createDdlWriter(Pair pair) {
    return createDdlWriter(pair.platform, pair.prefix);
  }

  private PlatformDdlWriter createDdlWriter(DatabasePlatform platform, String prefix) {
    return new PlatformDdlWriter(platform, serverConfig, prefix, migrationConfig);
  }

  /**
   * Write the migration xml.
   */
  protected boolean writeMigrationXml(Migration dbMigration, File resourcePath, String fullVersion) {

    String modelFile = fullVersion + migrationConfig.getModelSuffix();
    File file = new File(resourcePath, modelFile);
    if (file.exists()) {
      return false;
    }
    String comment = migrationConfig.isIncludeGeneratedFileComment() ? GENERATED_COMMENT : null;
    MigrationXmlWriter xmlWriter = new MigrationXmlWriter(comment);
    xmlWriter.write(dbMigration, file);
    return true;
  }

  /**
   * Set default server and platform if necessary.
   */
  protected void setDefaults() {
    if (server == null) {
      setServer(Ebean.getDefaultServer());
    }
    if (databasePlatform == null && platforms.isEmpty()) {
      // not explicitly set not set a list of platforms so
      // default to the platform of the default server
      databasePlatform = server.getDatabasePlatform();
      logger.debug("set platform to {}", databasePlatform.getName());
    }
  }

  /**
   * Return the file path to write the xml and sql to.
   */
  protected File getMigrationDirectory() {

    // path to src/main/resources in typical maven project
    File resourceRootDir = new File(pathToResources);
    String resourcePath = migrationConfig.getMigrationPath();

    // expect to be a path to something like - src/main/resources/dbmigration/model
    File path = new File(resourceRootDir, resourcePath);
    if (!path.exists()) {
      if (!path.mkdirs()) {
        logger.debug("Unable to ensure migration directory exists at {}", path.getAbsolutePath());
      }
    }
    return path;
  }


  /**
   * Return the model directory (relative to the migration directory).
   */
  protected File getModelDirectory(File migrationDirectory, TenantBeanType tenantBeanType) {
    String modelPath = migrationConfig.getModelPath();
    if (modelPath == null || modelPath.isEmpty()) {
      return migrationDirectory;
    }
    File modelDir = new File(migrationDirectory, migrationConfig.getModelPath());
    if (tenantBeanType != TenantBeanType.ALL) {
      modelDir = new File(modelDir, tenantBeanType.name().toLowerCase());
    }
    if (!modelDir.exists() && !modelDir.mkdirs()) {
      logger.debug("Unable to ensure migration model directory exists at {}", modelDir.getAbsolutePath());
    }
    return modelDir;
  }

  /**
   * Return the DatabasePlatform given the platform key.
   */
  protected DatabasePlatform getPlatform(Platform platform) {
    switch (platform) {
      case H2:
        return new H2Platform();
      case POSTGRES:
        return new PostgresPlatform();
      case MYSQL:
        return new MySqlPlatform();
      case ORACLE:
        return new OraclePlatform();
      case SQLSERVER:
        return new SqlServerPlatform();
      case DB2:
        return new DB2Platform();
      case SQLITE:
        return new SQLitePlatform();

      default:
        throw new IllegalArgumentException("Platform missing? " + platform);
    }
  }

  /**
   * Holds a platform and prefix. Used to generate multiple platform specific DDL
   * for a single migration.
   */
  public static class Pair {

    /**
     * The platform to generate the DDL for.
     */
    public final DatabasePlatform platform;

    /**
     * A prefix included into the file/resource names indicating the platform.
     */
    public final String prefix;

    
    public Pair(DatabasePlatform platform, String prefix) {
      this.platform = platform;
      this.prefix = prefix;
    }
  }

}
