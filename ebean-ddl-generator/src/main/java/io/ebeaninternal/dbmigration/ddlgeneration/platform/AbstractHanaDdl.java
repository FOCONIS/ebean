package io.ebeaninternal.dbmigration.ddlgeneration.platform;

import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.config.dbplatform.DbPlatformType;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlAlterTable;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlBuffer;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlHandler;
import io.ebeaninternal.dbmigration.ddlgeneration.DdlWrite;
import io.ebeaninternal.dbmigration.ddlgeneration.platform.BaseAlterTableWrite.AlterCmd;
import io.ebeaninternal.dbmigration.migration.AlterColumn;
import io.ebeaninternal.dbmigration.migration.Column;
import io.ebeaninternal.dbmigration.model.MTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractHanaDdl extends PlatformDdl {

  private static final Pattern ARRAY_PATTERN = Pattern.compile("(\\w+)\\s*\\[\\s*\\]\\s*(\\(\\d+\\))?", Pattern.CASE_INSENSITIVE);

  public AbstractHanaDdl(DatabasePlatform platform) {
    super(platform);
    this.addColumn = "add";
    this.alterColumn = "alter";
    this.columnDropDefault = " default null";
    this.columnSetDefault = " default";
    this.columnSetNotnull = " not null";
    this.columnSetNull = " null";
    this.dropColumn = "drop (";
    this.dropColumnSuffix = ")";
    this.dropConstraintIfExists = "drop constraint ";
    this.dropIndexIfExists = "drop index ";
    this.dropSequenceIfExists = "drop sequence ";
    this.dropTableCascade = " cascade";
    this.dropTableIfExists = "drop table ";
    this.fallbackArrayType = "nvarchar(1000)";
    this.historyDdl = new HanaHistoryDdl();
    this.identitySuffix = " generated by default as identity";
  }

  @Override
  public void alterColumnBaseAttributes(DdlWrite writer, AlterColumn alter, boolean onHistoryTable) {
    String tableName = alter.getTableName();
    String columnName = alter.getColumnName();
    String currentType = alter.getCurrentType();
    String type = alter.getType() != null ? alter.getType() : currentType;
    type = convert(type);
    currentType = convert(currentType);
    boolean notnull = (alter.isNotnull() != null) ? alter.isNotnull() : Boolean.TRUE.equals(alter.isCurrentNotnull());
    String notnullClause = notnull ? "not null" : "";
    String defaultValue = DdlHelp.isDropDefault(alter.getDefaultValue()) ? "null"
      : (alter.getDefaultValue() != null ? alter.getDefaultValue() : alter.getCurrentDefaultValue());
    String defaultValueClause = (defaultValue == null || defaultValue.isEmpty()) ? "" : "default " + defaultValue;

    if (!isConvertible(currentType, type)) {
      // add an intermediate conversion if possible
      if (isNumberType(currentType)) {
        // numbers can always be converted to decimal
        alterTable(writer, tableName).add(alterColumn, columnName, "decimal", notnullClause);

      } else if (isStringType(currentType)) {
        // strings can always be converted to nclob
        alterTable(writer, tableName).add(alterColumn, columnName, "nclob", notnullClause);
      }
    }

    alterTable(writer, tableName).add(alterColumn, columnName, type, defaultValueClause, notnullClause);
    if (!onHistoryTable) {
      disableHistoryDuringAlter(writer, tableName);
    }
  }

  @Override
  protected DdlAlterTable alterTable(DdlWrite writer, String tableName) {
    return writer.alterTable(tableName, HanaAlterTableWrite::new);
  }

  private static class HanaAlterTableWrite extends BaseAlterTableWrite {

    public HanaAlterTableWrite(String tableName) {
      super(tableName);
    }


    @Override
    public void write(Appendable target) throws IOException {
      // TODO Auto-generated method stub
      List<AlterCmd> newCmds = new ArrayList<>();
      Map<String, List<AlterCmd>> batches = new LinkedHashMap<>();
      Set<String> columns = new HashSet<>();
      for (AlterCmd cmd : cmds) {
        switch (cmd.operation) {
        case "add":
        case "alter":
        case "drop":
          if (cmd.column != null && !columns.add(cmd.column)) {
            // column already seen
            flushBatches(newCmds, batches);
            columns.clear();
          }
          batches.computeIfAbsent(cmd.operation, k -> new ArrayList<>()).add(cmd);
          break;
        default:
          flushBatches(newCmds, batches);
          columns.clear();
          newCmds.add(cmd);
        }
      }
      flushBatches(newCmds, batches);
      cmds = newCmds;

      super.write(target);
    }


    private void flushBatches(List<AlterCmd> newCmds, Map<String, List<AlterCmd>> batches) {
      for (List<AlterCmd> cmds : batches.values()) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cmds.size(); i++) {
          AlterCmd cmd = cmds.get(i);
          if (i == 0) {
            sb.append(cmd.operation).append(" (");
          } else {
            sb.append(",\n   ");
          }
          sb.append(cmd.column);
          if (cmd.suffix != null) {
            sb.append(' ').append(cmd.suffix);
          }
        }
        sb.append(")");
        newCmds.add(new AlterCmd(sb.toString(), null, null));
      }
      batches.clear();
    }
  }

//  @Override
//  protected StringBuilder alterTable(DdlWrite writer, String tableName, String column, String cmd) {
//    boolean batchableCmd = cmd.equals("add") || cmd.equals("alter") || cmd.equals("drop");
//    if (batchableCmd) {
//      return writer.alterTables().computeIfAbsent(tableName + ":" + cmd, k -> {
//        DdlAlterTableWrite alterWrite = new DdlAlterTableWrite(tableName);
//        if (batchableCmd) {
//          alterWrite.setMerge(cmd + " (", ",", ")");
//        }
//        return alterWrite;
//      }).append("");
//    } else {
//      return super.alterTable(writer, tableName, column, cmd);
//    }
//  }


  @Override
  public void alterColumnDefaultValue(DdlWrite writer, String tableName, String columnName, String defaultValue) {
    // done in alterColumnBaseAttributes
  }

  @Override
  public void alterColumnNotnull(DdlWrite writer, String tableName, String columnName, boolean notnull) {
    // done in alterColumnBaseAttributes
  }

  @Override
  public void alterColumnType(DdlWrite writer, String tableName, String columnName, String type,
      boolean onHistoryTable) {
    // done in alterColumnBaseAttributes
  }

  @Override
  protected String convertArrayType(String logicalArrayType) {
    Matcher matcher = ARRAY_PATTERN.matcher(logicalArrayType);
    if (matcher.matches()) {
      return convert(matcher.group(1)) + " array" + (matcher.group(2) == null ? "" : matcher.group(2));
    } else {
      return fallbackArrayType;
    }
  }

  @Override
  public void alterTableAddUniqueConstraint(DdlWrite writer, String tableName, String uqName, String[] columns, String[] nullableColumns) {
    if (nullableColumns == null || nullableColumns.length == 0) {
      super.alterTableAddUniqueConstraint(writer, tableName, uqName, columns, nullableColumns);
    } else {
      writer.index().appendStatement("-- cannot create unique index \"" + uqName + "\" on table \"" + tableName + "\" with nullable columns");
    }
  }

  @Override
  public void alterTableDropUniqueConstraint(DdlWrite writer, String tableName, String uniqueConstraintName) {
    DdlBuffer buffer = writer.dropDependencies();

    buffer.append("delimiter $$").newLine();
    buffer.append("do").newLine();
    buffer.append("begin").newLine();
    buffer.append("declare exit handler for sql_error_code 397 begin end").endOfStatement();
    buffer.append("exec 'alter table ").append(tableName).append(" ").append(dropUniqueConstraint).append(" ")
      .append(maxConstraintName(uniqueConstraintName)).append("'").endOfStatement();
    buffer.append("end").endOfStatement();
    buffer.append("$$");
    buffer.endOfStatement();
  }

  @Override
  public void alterTableDropConstraint(DdlWrite writer, String tableName, String constraintName) {
    alterTableDropUniqueConstraint(writer, tableName, constraintName);
  }

  /**
   * It is rather complex to delete a column on HANA as there must not exist any
   * foreign keys. That's why we call a user stored procedure here
   */
  @Override
  public void alterTableDropColumn(DdlWrite writer, String tableName, String columnName, boolean onHistoryTable) {
    disableHistoryDuringAlter(writer, tableName);
    writer.apply().append("CALL usp_ebean_drop_column('").append(tableName)
        .append("', '").append(columnName).append("')").endOfStatement();
    if (!onHistoryTable) {
      disableHistoryDuringAlter(writer, tableName);
    }

  }

  public void alterTableAddColumn(DdlWrite writer, String tableName, Column column, boolean onHistoryTable,
      String defaultValue) {
    super.alterTableAddColumn(writer, tableName, column, onHistoryTable, defaultValue);
    if (!onHistoryTable) {
      disableHistoryDuringAlter(writer, tableName);
    }
  }


  private void disableHistoryDuringAlter(DdlWrite writer, String tableName) {
    MTable table = writer.getTable(tableName);
    if (table != null && table.isWithHistory()) {
      DdlAlterTable alter = alterTable(writer, tableName);
      if (!alter.isHistoryHandled()) {
        HanaHistoryDdl historyDdl = (HanaHistoryDdl) this.historyDdl;
        writer.apply().appendStatement(historyDdl.disableSystemVersioning(tableName));
        writer.postAlter().appendStatement(historyDdl.enableSystemVersioning(tableName, false));
        alter.setHistoryHandled();
      }
    }
  };

  /**
   * Check if a data type can be converted to another data type. Data types can't
   * be converted if the target type has a lower precision than the source type.
   *
   * @param sourceType The source data type
   * @param targetType the target data type
   * @return {@code true} if the type can be converted, {@code false} otherwise
   */
  private boolean isConvertible(String sourceType, String targetType) {
    if (Objects.equals(sourceType, targetType)) {
      return true;
    }

    if (sourceType == null || targetType == null) {
      return true;
    }

    if ("bigint".equals(sourceType)) {
      if ("integer".equals(targetType) || "smallint".equals(targetType) || "tinyint".equals(targetType)) {
        return false;
      }
    } else if ("integer".equals(sourceType)) {
      if ("smallint".equals(targetType) || "tinyint".equals(targetType)) {
        return false;
      }
    } else if ("smallint".equals(sourceType)) {
      if ("tinyint".equals(targetType)) {
        return false;
      }
    } else if ("double".equals(sourceType)) {
      if ("real".equals(targetType)) {
        return false;
      }
    }

    DbPlatformType dbPlatformSourceType = DbPlatformType.parse(sourceType);

    if ("float".equals(dbPlatformSourceType.getName())) {
      if ("real".equals(targetType)) {
        return false;
      }
    } else if ("varchar".equals(dbPlatformSourceType.getName()) || "nvarchar".equals(dbPlatformSourceType.getName())) {
      DbPlatformType dbPlatformTargetType = DbPlatformType.parse(targetType);
      if ("varchar".equals(dbPlatformTargetType.getName()) || "nvarchar".equals(dbPlatformTargetType.getName())) {
        if (dbPlatformSourceType.getDefaultLength() > dbPlatformTargetType.getDefaultLength()) {
          return false;
        }
      }
    } else if ("decimal".equals(dbPlatformSourceType.getName())) {
      DbPlatformType dbPlatformTargetType = DbPlatformType.parse(targetType);
      if ("decimal".equals(dbPlatformTargetType.getName())) {
        if (dbPlatformSourceType.getDefaultLength() > dbPlatformTargetType.getDefaultLength()
          || dbPlatformSourceType.getDefaultScale() > dbPlatformTargetType.getDefaultScale()) {
          return false;
        }
      }
    }

    return true;
  }

  private boolean isNumberType(String type) {
    return type != null
      && ("bigint".equals(type) || "integer".equals(type) || "smallint".equals(type) || "tinyint".equals(type)
      || type.startsWith("float") || "real".equals(type) || "double".equals(type) || type.startsWith("decimal"));
  }

  private boolean isStringType(String type) {
    return type != null
      && (type.startsWith("varchar") || type.startsWith("nvarchar") || "clob".equals(type) || "nclob".equals(type));
  }
}
