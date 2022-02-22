package io.ebeaninternal.dbmigration.ddlgeneration;

import java.util.Set;

/**
 * An alter specification
 * 
 * @author TODO Adjust eclipse.ini, FOCONIS AG
 *
 */
public interface DdlAlterSpecification {

  StringBuilder cmd(String cmd);

  StringBuilder cmd(String cmd, String column);

  /**
   * Commands executed BEFORE altering a certain table.
   */
  Set<String> pre();

  /**
   * Commands executed AFTER altering a certain table.
   */
  Set<String> post();

}
