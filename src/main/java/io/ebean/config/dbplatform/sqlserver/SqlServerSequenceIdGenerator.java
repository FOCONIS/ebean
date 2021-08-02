package io.ebean.config.dbplatform.sqlserver;

import io.ebean.BackgroundExecutor;
import io.ebean.config.dbplatform.SequenceBatchIdGenerator;

import javax.sql.DataSource;

/**
 * MySql specific sequence Id Generator.
 */
public class SqlServerSequenceIdGenerator extends SequenceBatchIdGenerator {

  /**
   * Construct given a dataSource and sql to return the next sequence value.
   */
  public SqlServerSequenceIdGenerator(BackgroundExecutor be, DataSource ds, String seqName, int batchSize) {
    super(be, ds, seqName, batchSize);
  }

  @Override
  public String getSql(int batchSize) {
    StringBuilder sb = new StringBuilder("DECLARE @range_first_value_output sql_variant ;");
    sb.append("EXEC sys.sp_sequence_get_range @sequence_name = N'");
    sb.append(seqName);
    sb.append("', @range_size = 4, @range_first_value = @range_first_value_output OUTPUT ;");
    sb.append("SELECT @range_first_value_output AS curr_val;");
    
    return sb.toString();
  }
}
