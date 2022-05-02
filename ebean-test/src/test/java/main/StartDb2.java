package main;

import io.ebean.docker.commands.Db2Container;

public class StartDb2 {

  public static void main(String[] args) {
    Db2Container.newBuilder("11.5.6.0a")
      .dbName("unit")
      .user("unit")
      .password("unit")
      // to change collation, charset and other parameters like pagesize:
      .configOptions("USING CODESET UTF-8 TERRITORY DE COLLATE USING IDENTITY PAGESIZE 32768")
      .configOptions("USING STRING_UNITS CODEUNITS32")
      .build()
      .startWithDropCreate();
  }
}
