package io.ebeaninternal.server.deploy;

import io.ebeaninternal.server.query.STreeProperty;
import io.ebeaninternal.server.query.SqlJoinType;
import io.ebeaninternal.server.type.ScalarType;

import java.util.List;

/**
 * Abstract base for dynamic properties.
 */
abstract class DynamicPropertyBase implements STreeProperty {

  private final String name;
  final String fullName;
  private final String elPrefix;
  final ScalarType<?> scalarType;
  private final BeanProperty baseProp;

  DynamicPropertyBase(String name, String fullName, String elPrefix, ScalarType<?> scalarType, BeanProperty baseProp) {
    this.name = name;
    this.fullName = fullName;
    this.elPrefix = elPrefix;
    this.scalarType = scalarType;
    this.baseProp = baseProp;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getFullBeanName() {
    return fullName;
  }

  @Override
  public boolean isId() {
    return false;
  }

  @Override
  public boolean isEmbedded() {
    return false;
  }

  @Override
  public boolean isFormula() {
    return false;
  }

  @Override
  public String getElPrefix() {
    return elPrefix;
  }

  @Override
  public ScalarType<?> getScalarType() {
    return scalarType;
  }

  @Override
  public void buildRawSqlSelectChain(String prefix, List<String> selectChain) {
    // do nothing, only for RawSql
  }

  @Override
  public void loadIgnore(DbReadContext ctx) {
    scalarType.loadIgnore(ctx.getDataReader());
  }

  @Override
  public void appendFrom(DbSqlContext ctx, SqlJoinType joinType, String manyWhere) {
    if (baseProp != null) {
      baseProp.appendFrom(ctx, joinType, manyWhere);
    }
  }

  @Override
  public String getEncryptKeyAsString() {
    return null;
  }
}
