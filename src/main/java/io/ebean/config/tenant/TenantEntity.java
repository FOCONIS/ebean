package io.ebean.config.tenant;
/**
 * This is a model for a tenant. Can be extended.
 * 
 * @author Roland Praml, FOCONIS AG
 */
public class TenantEntity {
  
  private Object id;
  
  public TenantEntity() {}
  
  public TenantEntity(Object id) {
    super();
    this.id = id;
  }

  public void setId(Object id) {
    this.id = id;
  }
  
  public Object getId() {
    return id;
  }
  
  @Override
  public String toString() {
    return String.valueOf(id);
  }

}
