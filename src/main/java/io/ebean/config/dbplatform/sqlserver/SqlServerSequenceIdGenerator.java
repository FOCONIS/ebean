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
    StringBuilder sb = new StringBuilder();
    sb.append("DECLARE @range_first_value_output sql_variant ;"); // start value
    sb.append("DECLARE @range_last_value_output sql_variant ;"); // end value
    
    // stored procedure:
    // https://docs.microsoft.com/de-de/sql/relational-databases/system-stored-procedures/sp-sequence-get-range-transact-sql
    sb.append("EXEC sys.sp_sequence_get_range @sequence_name = N'").append(seqName).append("' ");
    sb.append(",@range_size = ").append(batchSize);
    sb.append(",@range_first_value = @range_first_value_output OUTPUT");
    sb.append(",@range_last_value = @range_last_value_output OUTPUT;");
    
    sb.append("SELECT DISTINCT number FROM master..[spt_values] WHERE number BETWEEN @range_first_value_output AND @range_last_value_output;");
    
    return sb.toString();
  }
}
