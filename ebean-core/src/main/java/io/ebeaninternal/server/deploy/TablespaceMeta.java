package io.ebeaninternal.server.deploy;

import java.util.Objects;

public final class TablespaceMeta {

  private String tablespaceName;
  private String indexTablespace;
  private String lobTablespace;

  public TablespaceMeta() {
    
  }
  
  public TablespaceMeta(String tablespaceName, String indexTablespace, String lobTablespace) {
    this.tablespaceName = tablespaceName;
    this.indexTablespace = indexTablespace;
    this.lobTablespace = lobTablespace;
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
  
  public String getLobTablespace() {
    return lobTablespace;
  }

  public void setLobTablespace(String lobTablespace) {
    this.lobTablespace = lobTablespace;
  }

  @Override
  public int hashCode() {
    return Objects.hash(indexTablespace, tablespaceName, lobTablespace);
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
        && Objects.equals(tablespaceName, other.tablespaceName)
        && Objects.equals(lobTablespace, other.lobTablespace);
  }



  @Override
  public String toString() {
    return "tablespace=" + tablespaceName + ", indexTablespace=" + indexTablespace + ", lobTablespace=" + lobTablespace;
  }

  public boolean isSet() {
    return tablespaceName != null || indexTablespace != null || lobTablespace != null;
  }
}
