package io.ebeaninternal.server.type;

import io.ebean.ModifyAwareType;
import io.ebean.annotation.MutationDetection;
import io.ebean.config.dbplatform.DbPlatformType;
import io.ebean.core.type.PostgresHelper;
import io.ebean.core.type.ScalarJsonManager;

final class TypeJsonManager implements ScalarJsonManager {

  private final boolean postgres;
  private final Object mapper;
  private final MutationDetection mutationDetection;

  TypeJsonManager(boolean postgres, Object mapper, MutationDetection mutationDetection) {
    this.postgres = postgres;
    this.mapper = mapper;
    this.mutationDetection = mutationDetection;
  }

  @Override
  public MutationDetection mutationDetection() {
    return mutationDetection;
  }

  @Override
  public Object mapper() {
    return mapper;
  }

  @Override
  public String postgresType(int dbType) {
    if (postgres) {
      switch (dbType) {
        case DbPlatformType.JSON:
          return PostgresHelper.JSON_TYPE;
        case DbPlatformType.JSONB:
          return PostgresHelper.JSONB_TYPE;
      }
    }
    return null;
  }

  /**
   * Return true if the value should be considered dirty (and included in an update).
   */
  static boolean checkIsDirty(Object value) {
    if (value instanceof ModifyAwareType) {
      return checkModifyAware(value);
    }
    return true;
  }

  private static boolean checkModifyAware(Object value) {
    ModifyAwareType modifyAware = (ModifyAwareType) value;
    if (modifyAware.isMarkedDirty()) {
      // reset the dirty state (consider not dirty after update)
      modifyAware.setMarkedDirty(false);
      return true;
    } else {
      return false;
    }
  }

}
