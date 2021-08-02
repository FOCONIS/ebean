package io.ebean.config.dbplatform.mysql;

import io.ebean.BackgroundExecutor;
import io.ebean.config.dbplatform.SequenceBatchIdGenerator;

import javax.sql.DataSource;

/**
 * MySql specific sequence Id Generator.
 */
public class MySqlSequenceIdGenerator extends SequenceBatchIdGenerator {

  private final String baseSql;
  private final String unionBaseSql;

  /**
   * Construct given a dataSource and sql to return the next sequence value.
   */
  public MySqlSequenceIdGenerator(BackgroundExecutor be, DataSource ds, String seqName, int batchSize) {
    super(be, ds, seqName, batchSize);
    // https://mariadb.com/kb/en/next-value-for-sequence_name/
    this.baseSql = "select next value for " + seqName;
    this.unionBaseSql = " union " + baseSql;
  }

  @Override
  public String getSql(int batchSize) {

    StringBuilder sb = new StringBuilder();
    sb.append(baseSql);
    for (int i = 1; i < batchSize; i++) {
      sb.append(unionBaseSql);
    }
    return sb.toString();
  }
}
