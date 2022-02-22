package io.ebeaninternal.dbmigration.ddlgeneration;

import java.io.IOException;

@FunctionalInterface
public interface DdlAlterMerger {

  String merge(Object write);

}
