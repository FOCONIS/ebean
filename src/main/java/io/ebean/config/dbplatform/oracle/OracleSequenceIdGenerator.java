package io.ebean.config.dbplatform.oracle;

import io.ebean.BackgroundExecutor;
import io.ebean.TenantContext;
import io.ebean.config.TenantDataSourceProvider;
import io.ebean.config.dbplatform.SequenceIdGenerator;

/**
 * Oracle specific sequence Id Generator.
 */
public class OracleSequenceIdGenerator extends SequenceIdGenerator {

  private final String baseSql;

  /**
   * Construct given a dataSource and sql to return the next sequence value.
   * @param perTenant 
   */
  public OracleSequenceIdGenerator(BackgroundExecutor be, TenantDataSourceProvider ds, String seqName, int batchSize, boolean perTenant, TenantContext tenantContext) {
    super(be, ds, seqName, batchSize, perTenant, tenantContext);
    this.baseSql = "select " + seqName + ".nextval, a from (select level as a FROM dual CONNECT BY level <= ";
  }

  @Override
  public String getSql(int batchSize) {
    return baseSql + batchSize + ")";
  }
}
