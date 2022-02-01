package io.ebeaninternal.server.deploy;

import java.util.Objects;

public final class TablespaceMeta {


  private String tablespaceName;
  private String indexTablespace;

  public TablespaceMeta() {
    
  }
  
  public TablespaceMeta(String tablespaceName, String indexTablespace) {
    this.tablespaceName = tablespaceName;
    this.indexTablespace = indexTablespace;
  }

  public String getTablespaceName() {
    return tablespaceName;
  }

  public void setTablespaceName(String tablespaceName) {
    this.tablespaceName = tablespaceName;
  }

  public String getIndexTablespace() {
    return indexTablespace;
  }

  public void setIndexTablespace(String indexTablespace) {
    this.indexTablespace = indexTablespace;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(indexTablespace, tablespaceName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TablespaceMeta other = (TablespaceMeta) obj;
    return Objects.equals(indexTablespace, other.indexTablespace)
        && Objects.equals(tablespaceName, other.tablespaceName);
  }



  @Override
  public String toString() {
    return "tablespace=" + tablespaceName + ", indexTablespace=" + indexTablespace;
  }

  public boolean isSet() {
    return tablespaceName != null || indexTablespace != null;
  }
}
