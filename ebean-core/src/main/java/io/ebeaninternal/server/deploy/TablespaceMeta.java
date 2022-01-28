package io.ebeaninternal.server.deploy;

public final class TablespaceMeta {

  private String tablespaceName;
  private String indexTablespace;

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

}
