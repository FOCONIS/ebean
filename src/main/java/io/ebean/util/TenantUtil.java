package io.ebean.util;


public class TenantUtil {
  public static final String SHARED_SCHEMA = "${shared_schema}";
  public static final String TENANT_SCHEMA = "${tenant_schema}";
  private TenantUtil() {}
  
  public static String applySchemas(String sql, String sharedSchema, String tenantSchema) {
    if (sharedSchema != null) {
      sql = StringHelper.replaceString(sql, SHARED_SCHEMA, sharedSchema);
    }
    if (tenantSchema != null) {
      sql = StringHelper.replaceString(sql, TENANT_SCHEMA, tenantSchema);
    }
    return sql;
  }
  
}
