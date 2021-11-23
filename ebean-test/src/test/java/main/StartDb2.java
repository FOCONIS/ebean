package main;

import io.ebean.docker.commands.Db2Config;
import io.ebean.docker.commands.Db2Container;

public class StartDb2 {

  public static void main(String[] args) {

    Db2Config config = new Db2Config("11.5.6.0a");
    config.setDbName("unit");
    config.setUser("unit");
    config.setPassword("unit");

    // by default this mysql docker collation is case sensitive
    // using utf8mb4_bin
    //
    // when changing to a CI collation (e.g. utf8mb4_unicode_ci) we also set
    // ebean.<db>.caseSensitiveCollation=false
    // ... such that tests now take that into account
//    config.setCollation("default");
//    config.setCollation("utf8mb4_unicode_ci");
//    config.setCharacterSet("utf8mb4");

    Db2Container container = new Db2Container(config);
    container.startWithDropCreate();
  }
}
