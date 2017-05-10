package io.ebean.bean;

import java.io.Serializable;

/**
 * We use a combined key, if this serverCache is per tenant.
 */
public final class TenantCacheKey implements Serializable {
  private static final long serialVersionUID = 1L;

  final Object tenantId;
  final Object key;

  public TenantCacheKey(Object tenantId, Object key) {
    super();
    this.tenantId = tenantId;
    this.key = key;
  }

  @Override
  public int hashCode() {
    int result = key.hashCode();
    result = 31 * result + ((tenantId == null) ? 0 : tenantId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TenantCacheKey) {
      TenantCacheKey other = (TenantCacheKey) obj;
      if (other.key.equals(this.key)) {
        if (other.tenantId == null && this.tenantId == null) {
          return true;
        } else if (this.tenantId != null) {
          return this.tenantId.equals(other.tenantId);
        }
      }
    }
    return false;
  }
}