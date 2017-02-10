package io.ebean.config.tenant;


import io.ebean.config.PropertiesWrapper;
import io.ebean.config.dbplatform.DatabasePlatform;

public class TableTenantRepository extends QueryTenantRepository {

  private String tableName = "tenant";
  
  @Override
  public void configure(DatabasePlatform platform, PropertiesWrapper prop) {
    tableName = prop.get("tableName");
    init = "CREATE TABLE " + tableName + " (id integer not null, constraint pk primary key (id));"; 
    query = "SELECT id from " + tableName;
    create = "INSERT INTO " + tableName + " (id) VALUES (?)";
    delete = "DELETE FROM " + tableName + " WHERE id = ?";
  }

}
