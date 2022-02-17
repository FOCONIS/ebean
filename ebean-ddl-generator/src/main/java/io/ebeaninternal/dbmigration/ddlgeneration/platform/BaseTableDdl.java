package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.annotation.Platform;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.DbConstraintNaming;
import io.ebean.config.NamingConvention;
import io.ebean.config.dbplatform.DbHistorySupport;
import io.ebean.config.dbplatform.IdType;
import io.ebean.util.StringHelper;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlOptions;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.TableDdl;
import io.ebeaninternal.dbmigration.ddlgeneration.platform.util.IndexSet;
import io.ebeaninternal.dbmigration.migration.AddColumn;
import io.ebeaninternal.dbmigration.migration.AddHistoryTable;
import io.ebeaninternal.dbmigration.migration.AddTableComment;
import io.ebeaninternal.dbmigration.migration.AddUniqueConstraint;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.dbmigration.migration.AlterForeignKey;
import io.ebeaninternal.dbmigration.migration.Column;
import io.ebeaninternal.dbmigration.migration.CreateIndex;
import io.ebeaninternal.dbmigration.migration.CreateTable;
import io.ebeaninternal.dbmigration.migration.DdlScript;
import io.ebeaninternal.dbmigration.migration.DropColumn;
import io.ebeaninternal.dbmigration.migration.DropHistoryTable;
import io.ebeaninternal.dbmigration.migration.DropIndex;
import io.ebeaninternal.dbmigration.migration.DropTable;
import io.ebeaninternal.dbmigration.migration.ForeignKey;
import io.ebeaninternal.dbmigration.migration.UniqueConstraint;
import io.ebeaninternal.dbmigration.model.MTable;
import io.ebeaninternal.dbmigration.model.MTableIdentity;
import io.ebeaninternal.server.deploy.IdentityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.ebean.util.StringHelper.replace;
import static io.ebeaninternal.api.PlatformMatch.matchPlatform;
import static io.ebeaninternal.dbmigration.ddlgeneration.platform.SplitColumns.split;

/**
 * Base implementation for 'create table' and 'alter table' statements.
 */
public class BaseTableDdl implements TableDdl {

  enum HistorySupport {
    NONE,
    SQL2011,
    TRIGGER_BASED
  }

  protected final DbConstraintNaming naming;

  protected final NamingConvention namingConvention;

  protected final PlatformDdl platformDdl;

  protected final String historyTableSuffix;

  /**
   * Used to check that indexes on foreign keys should be skipped as a unique index on the columns
   * already exists.
   */
  protected final IndexSet indexSet = new IndexSet();

  /**
   * Used when unique constraints specifically for OneToOne can't be created normally (MsSqlServer).
   */
  protected final List<Column> externalUnique = new ArrayList<>();

  protected final List<UniqueConstraint> externalCompoundUnique = new ArrayList<>();

  // counters used when constraint names are truncated due to maximum length
  // and these counters are used to keep the constraint name unique
  protected int countCheck;
  protected int countUnique;
  protected int countForeignKey;
  protected int countIndex;

  /**
   * Base tables that have associated history tables that need their triggers/functions regenerated as
   * columns have been added, removed, included or excluded.
   */
  protected final Map<String, HistoryTableUpdate> regenerateHistoryTriggers = new LinkedHashMap<>();

  private final boolean strictMode;

  private final HistorySupport historySupport;

  /**
   * Helper class that is used to execute the migration ddl before and after the migration action.
   */
  private class DdlMigrationHelp {
    private final List<String> before;
    private final List<String> after;
    private final String tableName;
    private final String columnName;
    private final String defaultValue;
    private final boolean withHistory;

    /**
     * Constructor for DdlMigrationHelp when adding a NEW column.
     */
    DdlMigrationHelp(String tableName, Column column, boolean withHistory) {
      this.tableName = tableName;
      this.columnName = column.getName();
      this.defaultValue = platformDdl.convertDefaultValue(column.getDefaultValue());
      boolean alterNotNull = Boolean.TRUE.equals(column.isNotnull());
      if (column.getBefore().isEmpty() && alterNotNull && defaultValue == null) {
        handleStrictError(tableName, columnName);
      }
      before = getScriptsForPlatform(column.getBefore());
      after = getScriptsForPlatform(column.getAfter());
      this.withHistory = withHistory;
    }

    /**
     * Constructor for DdlMigrationHelp when altering a column.
     */
    DdlMigrationHelp(AlterColumn alter) {
      this.tableName = alter.getTableName();
      this.columnName = alter.getColumnName();
      String tmp = alter.getDefaultValue() != null ? alter.getDefaultValue() : alter.getCurrentDefaultValue();
      this.defaultValue = platformDdl.convertDefaultValue(tmp);
      boolean alterNotNull = Boolean.TRUE.equals(alter.isNotnull());
      // here we add the platform's default update script
      withHistory = isTrue(alter.isWithHistory());
      if (alter.getBefore().isEmpty() && alterNotNull) {
        if (defaultValue == null) {
          handleStrictError(tableName, columnName);
        }
        before = Collections.singletonList(platformDdl.getUpdateNullWithDefault());
      } else {
        before = getScriptsForPlatform(alter.getBefore());
      }
      after = getScriptsForPlatform(alter.getAfter());
    }

    void write(DdlWrite writer) {
      if (!before.isEmpty()) {
        write.apply().end();
      }
      if (!before.isEmpty() && withHistory) {
        write.apply().append("-- NOTE: table has @History - special migration may be necessary").newLine();
      }
      for (String ddlScript : before) {
        write.apply().appendStatement(translate(ddlScript, tableName, columnName, defaultValue));
      }

      if (!after.isEmpty() && withHistory) {
        write.postAlter().append("-- NOTE: table has @History - special migration may be necessary").newLine();
      }
      // here we run post migration scripts
      for (String ddlScript : after) {
        write.postAlter().appendStatement(translate(ddlScript, tableName, columnName, defaultValue));
      }
      if (!after.isEmpty()) {
        write.postAlter().end();
      }
    }

    private List<String> getScriptsForPlatform(List<DdlScript> scripts) {
      Platform searchPlatform = platformDdl.getPlatform().getPlatform();
      for (DdlScript script : scripts) {
        if (matchPlatform(searchPlatform, script.getPlatforms())) {
          // just returns the first match (rather than appends them)
          return script.getDdl();
        }
      }
      return Collections.emptyList();
    }

    /**
     * Replaces Table name (${table}), Column name (${column}) and default value (${default}) in DDL.
     */
    private String translate(String ddl, String tableName, String columnName, String defaultValue) {
      String ret = replace(ddl, "${table}", tableName);
      ret = replace(ret, "${column}", columnName);
      return replace(ret, "${default}", defaultValue);
    }

    private void handleStrictError(String tableName, String columnName) {
      if (strictMode) {
        String message = "DB Migration of non-null column with no default value specified for: " + tableName + "." + columnName+" Use @DbDefault to specify a default value or specify dbMigration.setStrictMode(false)";
        throw new IllegalArgumentException(message);
      }
    }

    public String getDefaultValue() {
      return defaultValue;
    }
  }

  /**
   * Construct with a naming convention and platform specific DDL.
   */
  public BaseTableDdl(DatabaseConfig config, PlatformDdl platformDdl) {
    this.namingConvention = config.getNamingConvention();
    this.naming = config.getConstraintNaming();
    this.historyTableSuffix = config.getHistoryTableSuffix();
    this.platformDdl = platformDdl;
    this.platformDdl.configure(config);
    this.strictMode = config.isDdlStrictMode();
    DbHistorySupport hist = platformDdl.getPlatform().getHistorySupport();
    if (hist == null) {
      this.historySupport = HistorySupport.NONE;
    } else {
      this.historySupport = hist.isStandardsBased() ? HistorySupport.SQL2011 : HistorySupport.TRIGGER_BASED;
    }
  }

  /**
   * Reset counters and index set for each table processed.
   */
  protected void reset() {
    indexSet.clear();
    externalUnique.clear();
    externalCompoundUnique.clear();
    countCheck = 0;
    countUnique = 0;
    countForeignKey = 0;
    countIndex = 0;
  }

  /**
   * Generate the appropriate 'create table' and matching 'drop table' statements
   * and add them to the appropriate 'apply' and 'rollback' buffers.
   */
  @Override
  public void generate(DdlWrite writer, CreateTable createTable) {
    reset();

    String tableName = lowerTableName(createTable.getName());
    List<Column> columns = createTable.getColumn();
    List<Column> pk = determinePrimaryKeyColumns(columns);

    DdlIdentity identity = DdlIdentity.NONE;
    if ((pk.size() == 1)) {
      final IdentityMode identityMode = MTableIdentity.fromCreateTable(createTable);
      IdType idType = platformDdl.useIdentityType(identityMode.getIdType());
      String sequenceName = identityMode.getSequenceName();
      if (IdType.SEQUENCE == idType && (sequenceName == null || sequenceName.isEmpty())) {
        sequenceName = sequenceName(createTable, pk);
      }
      identity = new DdlIdentity(idType, identityMode, sequenceName);
    }

    String partitionMode = createTable.getPartitionMode();

    DdlBuffer apply = write.apply();
    apply.append(platformDdl.getCreateTableCommandPrefix()).append(" ").append(tableName).append(" (");
    writeTableColumns(apply, columns, identity);
    writeUniqueConstraints(apply, createTable);
    writeCompoundUniqueConstraints(apply, createTable);
    if (!pk.isEmpty()) {
      // defined on the columns
      if (partitionMode == null || !platformDdl.suppressPrimaryKeyOnPartition()) {
        writePrimaryKeyConstraint(apply, createTable.getPkName(), toColumnNames(pk));
      }
    }
    if (platformDdl.isInlineForeignKeys()) {
      writeInlineForeignKeys(apply, createTable);
    }
    apply.newLine().append(")");
    addTableStorageEngine(apply, createTable);
    addTableCommentInline(apply, createTable);
    if (partitionMode != null) {
      platformDdl.addTablePartition(apply, partitionMode, createTable.getPartitionColumn());
    }
    apply.endOfStatement();

    addComments(write, createTable);
    writeUniqueOneToOneConstraints(write, createTable);
    if (isTrue(createTable.isWithHistory())) {
      // create history with rollback before the
      // associated drop table is written to rollback
      createWithHistory(write, createTable.getName());
    }

    // add drop table to the rollback buffer - do this before
    // we drop the related sequence (if sequences are used)
    dropTable(write.dropAll(), tableName);

    if (identity.useSequence()) {
      writeSequence(write, identity);
    }

    // add blank line for a bit of whitespace between tables
    write.apply().end();
    write.dropAll().end();
    if (!platformDdl.isInlineForeignKeys()) {
      writeAddForeignKeys(write, createTable);
    }
  }

  private String sequenceName(CreateTable createTable, List<Column> pk) {
    return namingConvention.getSequenceName(createTable.getName(), pk.get(0).getName());
  }

  /**
   * Add table and column comments (separate from the create table statement).
   */
  private void addComments(DdlWrite writer, CreateTable createTable) {
    if (!platformDdl.isInlineComments()) {
      String tableComment = createTable.getComment();
      if (hasValue(tableComment)) {
        platformDdl.addTableComment(write, createTable.getName(), tableComment);
      }
      for (Column column : createTable.getColumn()) {
        if (!StringHelper.isNull(column.getComment())) {
          platformDdl.addColumnComment(write, createTable.getName(), column.getName(), column.getComment());
        }
      }
    }
  }

  /**
   * Add the table storage engine clause.
   */
  private void addTableStorageEngine(DdlBuffer apply, CreateTable createTable) {
    if (platformDdl.isIncludeStorageEngine()) {
      platformDdl.tableStorageEngine(apply, createTable.getStorageEngine());
    }
  }

  /**
   * Add the table comment inline with the create table statement.
   */
  private void addTableCommentInline(DdlBuffer apply, CreateTable createTable) {
    if (platformDdl.isInlineComments()) {
      String tableComment = createTable.getComment();
      if (!StringHelper.isNull(tableComment)) {
        platformDdl.inlineTableComment(apply, tableComment);
      }
    }
  }

  private void writeTableColumns(DdlBuffer apply, List<Column> columns, DdlIdentity identity) {
    platformDdl.writeTableColumns(apply, columns, identity);
  }

  /**
   * Specific handling of OneToOne unique constraints for MsSqlServer.
   * For all other DB platforms these unique constraints are done inline as per normal.
   */
  protected void writeUniqueOneToOneConstraints(DdlWrite write, CreateTable createTable) {
    String tableName = createTable.getName();
    for (Column col : externalUnique) {
      String uqName = col.getUniqueOneToOne();
      if (uqName == null) {
        uqName = col.getUnique();
      }
      String[] columnNames = {col.getName()};
      platformDdl.alterTableAddUniqueConstraint(write, tableName, uqName, columnNames, Boolean.TRUE.equals(col.isNotnull()) ? null : columnNames);
      write.dropAllIndex().appendStatement(platformDdl.dropIndex(uqName, tableName));
    }

    for (UniqueConstraint constraint : externalCompoundUnique) {
      String uqName = constraint.getName();
      String[] columnNames = split(constraint.getColumnNames());
      String[] nullableColumns = split(constraint.getNullableColumns());

      platformDdl.alterTableAddUniqueConstraint(write, tableName, uqName, columnNames, nullableColumns);
      write.dropAllIndex().appendStatement(platformDdl.dropIndex(uqName, tableName));
    }
  }

  protected void writeSequence(DdlWrite write, DdlIdentity identity) {
    String seqName = identity.getSequenceName();
    String createSeq = platformDdl.createSequence(seqName, identity);
    if (hasValue(createSeq)) {
      write.apply().append(createSeq).newLine();
      write.dropAll().appendStatement(platformDdl.dropSequence(seqName));
    }
  }

  protected void createWithHistory(DdlWrite write, String name) {
    MTable table = write.getTable(name);
    platformDdl.createWithHistory(write, table); 
  }

  protected void writeInlineForeignKeys(DdlBuffer apply, CreateTable createTable) {
    for (Column column : createTable.getColumn()) {
      String references = column.getReferences();
      if (hasValue(references)) {
        writeInlineForeignKey(apply, column);
      }
    }
    writeInlineCompoundForeignKeys(apply, createTable);
  }

  protected void writeInlineForeignKey(DdlBuffer apply, Column column) {
    String fkConstraint = platformDdl.tableInlineForeignKey(new WriteForeignKey(null, column));
    apply.append(",").newLine().append("  ").append(fkConstraint);
  }

  protected void writeInlineCompoundForeignKeys(DdlBuffer apply, CreateTable createTable) {
    for (ForeignKey key : createTable.getForeignKey()) {
      String fkConstraint = platformDdl.tableInlineForeignKey(new WriteForeignKey(null, key));
      apply.append(",").newLine().append("  ").append(fkConstraint);
    }
  }

  protected void writeAddForeignKeys(DdlWrite write, CreateTable createTable) {
    for (Column column : createTable.getColumn()) {
      String references = column.getReferences();
      if (hasValue(references)) {
        writeForeignKey(write, createTable.getName(), column);
      }
    }
    writeAddCompoundForeignKeys(write, createTable);
  }

  protected void writeAddCompoundForeignKeys(DdlWrite write, CreateTable createTable) {
    for (ForeignKey key : createTable.getForeignKey()) {
      writeForeignKey(write, new WriteForeignKey(createTable.getName(), key));
    }
  }

  protected void writeForeignKey(DdlWrite write, String tableName, Column column) {
    writeForeignKey(write, new WriteForeignKey(tableName, column));
  }

  protected void writeForeignKey(DdlWrite write, WriteForeignKey request) {
    String tableName = lowerTableName(request.table());
    if (request.indexName() != null) {
      // no matching unique constraint so add the index
      write.index().appendStatement(platformDdl.createIndex(new WriteCreateIndex(request.indexName(), tableName, request.cols(), false)));
    }
    alterTableAddForeignKey(write.getOptions(), write.index(), request);
    write.index().end();

    write.dropAllIndex().appendStatement(platformDdl.alterTableDropForeignKey(tableName, request.fkName()));
    if (hasValue(request.indexName())) {
      write.dropAllIndex().appendStatement(platformDdl.dropIndex(request.indexName(), tableName));
    }
    write.dropAllIndex().end();
  }

  protected void alterTableAddForeignKey(DdlOptions options, DdlBuffer buffer, WriteForeignKey request) {
    buffer.appendStatement(platformDdl.alterTableAddForeignKey(options, request));
  }

  protected void appendColumns(String[] columns, DdlBuffer buffer) {
    buffer.append(" (");
    for (int i = 0; i < columns.length; i++) {
      if (i > 0) {
        buffer.append(",");
      }
      buffer.append(lowerColumnName(columns[i].trim()));
    }
    buffer.append(")");
  }

  /**
   * Add 'drop table' statement to the buffer.
   */
  protected void dropTable(DdlBuffer buffer, String tableName) {
    buffer.appendStatement(platformDdl.dropTable(tableName));
  }

  /**
   * Add 'drop sequence' statement to the buffer.
   */
  protected void dropSequence(DdlBuffer buffer, String sequenceName) {
    buffer.appendStatement(platformDdl.dropSequence(sequenceName));
  }

  protected void writeCompoundUniqueConstraints(DdlBuffer apply, CreateTable createTable) {
    boolean inlineUniqueWhenNull = platformDdl.isInlineUniqueWhenNullable();
    for (UniqueConstraint uniqueConstraint : createTable.getUniqueConstraint()) {
      if (platformInclude(uniqueConstraint.getPlatforms())) {
        if (inlineUniqueWhenNull) {
          String uqName = uniqueConstraint.getName();
          apply.append(",").newLine();
          apply.append("  constraint ").append(uqName).append(" unique");
          appendColumns(split(uniqueConstraint.getColumnNames()), apply);
        } else {
          externalCompoundUnique.add(uniqueConstraint);
        }
      }
    }
  }

  private boolean platformInclude(String platforms) {
    return matchPlatform(platformDdl.getPlatform().getPlatform(), platforms);
  }

  /**
   * Write the unique constraints inline with the create table statement.
   */
  protected void writeUniqueConstraints(DdlBuffer apply, CreateTable createTable) {
    boolean inlineUniqueWhenNullable = platformDdl.isInlineUniqueWhenNullable();
    List<Column> columns = new WriteUniqueConstraint(createTable.getColumn()).uniqueKeys();
    for (Column column : columns) {
      if (Boolean.TRUE.equals(column.isNotnull()) || inlineUniqueWhenNullable) {
        // normal mechanism for adding unique constraint
        inlineUniqueConstraintSingle(apply, column);
      } else {
        // SqlServer & DB2 specific mechanism for adding unique constraints (that allow nulls)
        externalUnique.add(column);
      }
    }
  }

  /**
   * Write the unique constraint inline with the create table statement.
   */
  protected void inlineUniqueConstraintSingle(DdlBuffer buffer, Column column) {
    String uqName = column.getUnique();
    if (uqName == null) {
      uqName = column.getUniqueOneToOne();
    }
    buffer.append(",").newLine();
    buffer.append("  constraint ").append(uqName).append(" unique ");
    buffer.append("(");
    buffer.append(lowerColumnName(column.getName()));
    buffer.append(")");
  }

  /**
   * Write the primary key constraint inline with the create table statement.
   */
  protected void writePrimaryKeyConstraint(DdlBuffer buffer, String pkName, String[] pkColumns) {
    buffer.append(",").newLine();
    buffer.append("  constraint ").append(pkName).append(" primary key");
    appendColumns(pkColumns, buffer);
  }

  /**
   * Return as an array of string column names.
   */
  protected String[] toColumnNames(List<Column> columns) {
    String[] cols = new String[columns.size()];
    for (int i = 0; i < cols.length; i++) {
      cols[i] = columns.get(i).getName();
    }
    return cols;
  }

  /**
   * Convert the table lower case.
   */
  protected String lowerTableName(String name) {
    return naming.lowerTableName(name);
  }

  /**
   * Convert the column name to lower case.
   */
  protected String lowerColumnName(String name) {
    return naming.lowerColumnName(name);
  }

  /**
   * Return the list of columns that make the primary key.
   */
  protected List<Column> determinePrimaryKeyColumns(List<Column> columns) {
    List<Column> pk = new ArrayList<>(3);
    for (Column column : columns) {
      if (isTrue(column.isPrimaryKey())) {
        pk.add(column);
      }
    }
    return pk;
  }

  @Override
  public void generate(DdlWrite write, CreateIndex index) {
    if (platformInclude(index.getPlatforms())) {
      write.index().appendStatement(platformDdl.createIndex(new WriteCreateIndex(index)));
      write.dropAllIndex().appendStatement(platformDdl.dropIndex(index.getIndexName(), index.getTableName(), Boolean.TRUE.equals(index.isConcurrent())));
    }
  }

  @Override
  public void generate(DdlWrite write, DropIndex dropIndex) {
    if (platformInclude(dropIndex.getPlatforms())) {
      write.dropDependencies().appendStatement(platformDdl.dropIndex(dropIndex.getIndexName(), dropIndex.getTableName(), Boolean.TRUE.equals(dropIndex.isConcurrent())));
    }
  }

  @Override
  public void generate(DdlWrite write, AddUniqueConstraint constraint) {
    if (platformInclude(constraint.getPlatforms())) {
      if (DdlHelp.isDropConstraint(constraint.getColumnNames())) {
        platformDdl.alterTableDropUniqueConstraint(write, constraint.getTableName(), constraint.getConstraintName());

      } else {
        String[] cols = split(constraint.getColumnNames());
        String[] nullableColumns = split(constraint.getNullableColumns());
        platformDdl.alterTableAddUniqueConstraint(write, constraint.getTableName(), constraint.getConstraintName(), cols, nullableColumns);
      }
    }
  }

  @Override
  public void generate(DdlWrite write, AlterForeignKey alterForeignKey) {
    if (DdlHelp.isDropForeignKey(alterForeignKey.getColumnNames())) {
      write.index().appendStatement(platformDdl.alterTableDropForeignKey(alterForeignKey.getTableName(), alterForeignKey.getName()));
    } else {
      write.index().appendStatement(platformDdl.alterTableAddForeignKey(write.getOptions(), new WriteForeignKey(alterForeignKey)));
    }
  }

  /**
   * Add add history table DDL.
   */
  @Override
  public void generate(DdlWrite write, AddHistoryTable addHistoryTable) {
    platformDdl.addHistoryTable(write, addHistoryTable);
  }

  /**
   * Add drop history table DDL.
   */
  @Override
  public void generate(DdlWrite write, DropHistoryTable dropHistoryTable) {
    platformDdl.dropHistoryTable(write, dropHistoryTable);
  }

  @Override
  public void generateProlog(DdlWrite write) {
    platformDdl.generateProlog(write);
  }

  /**
   * Called at the end to generate additional ddl such as regenerate history triggers.
   */
  @Override
  public void generateEpilog(DdlWrite write) {
    if (!regenerateHistoryTriggers.isEmpty()) {
      platformDdl.lockTables(write.applyHistoryTrigger(), regenerateHistoryTriggers.keySet());

      for (HistoryTableUpdate update : this.regenerateHistoryTriggers.values()) {
        platformDdl.regenerateHistoryTriggers(write, update);
      }

      platformDdl.unlockTables(write.applyHistoryTrigger(), regenerateHistoryTriggers.keySet());
    }
    platformDdl.generateEpilog(write);
  }

  @Override
  public void generate(DdlWrite write, AddTableComment addTableComment) {
    if (hasValue(addTableComment.getComment())) {
      platformDdl.addTableComment(write, addTableComment.getName(), addTableComment.getComment());
    }
  }

  /**
   * Add add column DDL.
   */
  @Override
  public void generate(DdlWrite write, AddColumn addColumn) {
    String tableName = addColumn.getTableName();
    List<Column> columns = addColumn.getColumn();
    for (Column column : columns) {
      alterTableAddColumn(write, tableName, column, false, isTrue(addColumn.isWithHistory()));
    }
    if (isTrue(addColumn.isWithHistory()) && historySupport == HistorySupport.TRIGGER_BASED) {
      // make same changes to the history table
      String historyTable = historyTable(tableName);
      for (Column column : columns) {
        regenerateHistoryTriggers(tableName, HistoryTableUpdate.Change.ADD, column.getName());
        alterTableAddColumn(write, historyTable, column, true, true);
      }
    }
    for (Column column : columns) {
      if (hasValue(column.getReferences())) {
        writeForeignKey(write, tableName, column);
      }
    }
  }

  /**
   * Add drop table DDL.
   */
  @Override
  public void generate(DdlWrite write, DropTable dropTable) {
    dropTable(write.postAlter(), dropTable.getName());
    if (hasValue(dropTable.getSequenceCol())
        && platformDdl.getPlatform().getDbIdentity().isSupportsSequence()) {
      String sequenceName = dropTable.getSequenceName();
      if (!hasValue(sequenceName)) {
        sequenceName = namingConvention.getSequenceName(dropTable.getName(), dropTable.getSequenceCol());
      }
      dropSequence(write.apply(), sequenceName);
    }
  }

  /**
   * Add drop column DDL.
   */
  @Override
  public void generate(DdlWrite write, DropColumn dropColumn) {
    String tableName = dropColumn.getTableName();
    alterTableDropColumn(write, tableName, dropColumn.getColumnName());

    if (isTrue(dropColumn.isWithHistory())  && historySupport == HistorySupport.TRIGGER_BASED) {
      // also drop from the history table
      regenerateHistoryTriggers(tableName, HistoryTableUpdate.Change.DROP, dropColumn.getColumnName());
      alterTableDropColumn(write, historyTable(tableName), dropColumn.getColumnName());
    }
  }

  /**
   * Add all the appropriate changes based on the column changes.
   */
  @Override
  public void generate(DdlWrite write, AlterColumn alterColumn) {
    DdlMigrationHelp ddlHelp = new DdlMigrationHelp(alterColumn);
    ddlHelp.write(write);

    if (isTrue(alterColumn.isHistoryExclude())) {
      regenerateHistoryTriggers(alterColumn.getTableName(), HistoryTableUpdate.Change.EXCLUDE, alterColumn.getColumnName());
    } else if (isFalse(alterColumn.isHistoryExclude())) {
      regenerateHistoryTriggers(alterColumn.getTableName(), HistoryTableUpdate.Change.INCLUDE, alterColumn.getColumnName());
    }

    if (hasValue(alterColumn.getDropForeignKey())) {
      alterColumnDropForeignKey(write, alterColumn);
    }
    if (hasValue(alterColumn.getReferences())) {
      alterColumnAddForeignKey(write, alterColumn);
    }

    if (hasValue(alterColumn.getDropUnique())) {
      alterColumnDropUniqueConstraint(write, alterColumn);
    }
    if (hasValue(alterColumn.getUnique())) {
      alterColumnAddUniqueConstraint(write, alterColumn);
    }
    if (hasValue(alterColumn.getUniqueOneToOne())) {
      alterColumnAddUniqueOneToOneConstraint(write, alterColumn);
    }
    if (hasValue(alterColumn.getComment())) {
      alterColumnComment(write, alterColumn);
    }
    if (hasValue(alterColumn.getDropCheckConstraint())) {
      dropCheckConstraint(write, alterColumn, alterColumn.getDropCheckConstraint());
    }

    boolean alterCheckConstraint = hasValue(alterColumn.getCheckConstraint());

    if (alterCheckConstraint) {
      // drop constraint before altering type etc
      dropCheckConstraint(write, alterColumn, alterColumn.getCheckConstraintName());
    }
    boolean alterBaseAttributes = false;
    if (hasValue(alterColumn.getType())) {
      alterColumnType(write, alterColumn);
      alterBaseAttributes = true;
    }
    if (hasValue(alterColumn.getDefaultValue())) {
      alterColumnDefaultValue(write, alterColumn);
      alterBaseAttributes = true;
    }
    if (alterColumn.isNotnull() != null) {
      alterColumnNotnull(write, alterColumn);
      alterBaseAttributes = true;
    }
    if (alterBaseAttributes) {
      alterColumnBaseAttributes(write, alterColumn);
    }
    if (alterCheckConstraint) {
      // add constraint last (after potential type change)
      addCheckConstraint(write, alterColumn);
    }
  }

  private void alterColumnComment(DdlWrite write, AlterColumn alterColumn) {
    platformDdl.addColumnComment(write, alterColumn.getTableName(), alterColumn.getColumnName(), alterColumn.getComment());
  }

  /**
   * Return the name of the history table given the base table name.
   */
  protected String historyTable(String baseTable) {
    return baseTable + historyTableSuffix;
  }

  /**
   * Register the base table that we need to regenerate the history triggers on.
   */
  protected void regenerateHistoryTriggers(String baseTableName, HistoryTableUpdate.Change change, String column) {
    HistoryTableUpdate update = regenerateHistoryTriggers.computeIfAbsent(baseTableName, HistoryTableUpdate::new);
    update.add(change, column);
  }

  /**
   * This is mysql specific - alter all the base attributes of the column together.
   * Will be called, if there is a type, dbdefault or notnull change.
   */
  protected void alterColumnBaseAttributes(DdlWrite write, AlterColumn alter) {
    platformDdl.alterColumnBaseAttributes(write, alter);

    if (isTrue(alter.isWithHistory()) && alter.getType() != null && historySupport == HistorySupport.TRIGGER_BASED) {
      // mysql and sql server column type change allowing nulls in the history table
      // column
      regenerateHistoryTriggers(alter.getTableName(), HistoryTableUpdate.Change.ALTER, alter.getColumnName());
      AlterColumn alterHistoryColumn = new AlterColumn();
      alterHistoryColumn.setTableName(historyTable(alter.getTableName()));
      alterHistoryColumn.setColumnName(alter.getColumnName());
      alterHistoryColumn.setType(alter.getType());
      platformDdl.alterColumnBaseAttributes(write, alterHistoryColumn);
    }
  }

  protected void alterColumnDefaultValue(DdlWrite write, AlterColumn alter) {
    platformDdl.alterColumnDefaultValue(write, alter.getTableName(), alter.getColumnName(), alter.getDefaultValue());
  }

  protected void dropCheckConstraint(DdlWrite write, AlterColumn alter, String constraintName) {
    platformDdl.alterTableDropConstraint(write, alter.getTableName(), constraintName);
  }

  protected void addCheckConstraint(DdlWrite write, AlterColumn alter) {
    platformDdl.alterTableAddCheckConstraint(write, alter.getTableName(), alter.getCheckConstraintName(), alter.getCheckConstraint());
  }

  protected void alterColumnNotnull(DdlWrite write, AlterColumn alter) {
    platformDdl.alterColumnNotnull(write, alter.getTableName(), alter.getColumnName(), alter.isNotnull());
  }

  protected void alterColumnType(DdlWrite write, AlterColumn alter) {
    platformDdl.alterColumnType(write, alter.getTableName(), alter.getColumnName(), alter.getType());
    
    if (isTrue(alter.isWithHistory()) && historySupport == HistorySupport.TRIGGER_BASED) {
      regenerateHistoryTriggers(alter.getTableName(), HistoryTableUpdate.Change.ALTER, alter.getColumnName());
      // apply same type change to matching column in the history table
      platformDdl.alterColumnType(write, historyTable(alter.getTableName()), alter.getColumnName(), alter.getType());
    }
  }

  protected void alterColumnAddForeignKey(DdlWrite write, AlterColumn alterColumn) {
    alterTableAddForeignKey(write.getOptions(), write.index(), new WriteForeignKey(alterColumn));
  }

  protected void alterColumnDropForeignKey(DdlWrite write, AlterColumn alter) {
    write.dropDependencies().appendStatement(platformDdl.alterTableDropForeignKey(alter.getTableName(), alter.getDropForeignKey()));
  }

  protected void alterColumnDropUniqueConstraint(DdlWrite write, AlterColumn alter) {
    platformDdl.alterTableDropUniqueConstraint(write, alter.getTableName(), alter.getDropUnique());
  }

  protected void alterColumnAddUniqueOneToOneConstraint(DdlWrite write, AlterColumn alter) {
    addUniqueConstraint(write, alter, alter.getUniqueOneToOne());
  }

  protected void alterColumnAddUniqueConstraint(DdlWrite write, AlterColumn alter) {
    addUniqueConstraint(write, alter, alter.getUnique());
  }

  protected void addUniqueConstraint(DdlWrite write, AlterColumn alter, String uqName) {
    String[] cols = {alter.getColumnName()};
    boolean notNull = alter.isNotnull() != null ? alter.isNotnull() : Boolean.TRUE.equals(alter.isNotnull());
    platformDdl.alterTableAddUniqueConstraint(write, alter.getTableName(), uqName, cols, notNull ? null : cols);

    write.dropAllIndex().appendStatement(platformDdl.dropIndex(uqName, alter.getTableName()));
  }


  protected void alterTableDropColumn(DdlWrite write, String tableName, String columnName) {
    platformDdl.alterTableDropColumn(write, tableName, columnName);
  }

  protected void alterTableAddColumn(DdlWrite write, String tableName, Column column, boolean onHistoryTable, boolean withHistory) {
    DdlMigrationHelp help = new DdlMigrationHelp(tableName, column, withHistory);
    if (!onHistoryTable) {
      help.write(write);
    }

    platformDdl.alterTableAddColumn(write, tableName, column, onHistoryTable, help.getDefaultValue());
    final String comment = column.getComment();
    if (comment != null && !comment.isEmpty()) {
      platformDdl.addColumnComment(write, tableName, column.getName(), comment);
    }
  }

  protected boolean isFalse(Boolean value) {
    return value != null && !value;
  }

  /**
   * Return true if null or trimmed string is empty.
   */
  protected boolean hasValue(String value) {
    return value != null && !value.trim().isEmpty();
  }

  /**
   * Null safe Boolean true test.
   */
  protected boolean isTrue(Boolean value) {
    return Boolean.TRUE.equals(value);
  }

}
