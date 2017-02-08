package io.ebeaninternal.server.core;

import io.ebean.config.CurrentTenantProvider;
import io.ebean.config.TenantSchemaProvider;
import io.ebean.util.TenantUtil;

public class SchemaTranslateTenantContext extends DefaultTenantContext {

  private final TenantSchemaProvider schemaProvider;
  private final String sharedSchema;

  public SchemaTranslateTenantContext(CurrentTenantProvider currentTenantProvider,
      TenantSchemaProvider schemaProvider, String sharedSchema) {
    super(currentTenantProvider);
    this.schemaProvider = schemaProvider;
    this.sharedSchema = sharedSchema;
  }

  @Override
  public String translateSql(String sql) {
    Object tenantId = getTenantId();
    String tenantSchema = null;
    if (tenantId != null) {
      tenantSchema = schemaProvider.schema(tenantId);
    }
    return TenantUtil.applySchemas(sql, sharedSchema, tenantSchema);
  }

}
