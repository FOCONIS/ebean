package io.ebeaninternal.server.expression;

import io.ebean.event.BeanQueryRequest;
import io.ebean.util.SplitName;
import io.ebeaninternal.api.*;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.el.ElPropertyDeploy;
import io.ebeaninternal.server.el.ElPropertyValue;

/**
 * Base class for simple expressions.
 */
abstract class AbstractExpression implements SpiExpression {

  protected String propName;

  protected AbstractExpression(String propName) {
    this.propName = propName;
  }

  @Override
  public void prefixProperty(String path) {
    this.propName = path + "." + propName;
  }

  @Override
  public boolean naturalKey(NaturalKeyQueryData<?> data) {
    // by default can't use naturalKey cache
    return false;
  }

  @Override
  public Object getIdEqualTo(String idName) {
    // override on SimpleExpression
    return null;
  }

  @Override
  public SpiExpression copyForPlanKey() {
    return this;
  }

  @Override
  public String nestedPath(BeanDescriptor<?> desc) {
    return propertyNestedPath(propName, desc);
  }

  protected String propertyNestedPath(String propertyName, BeanDescriptor<?> desc) {
    if (propertyName != null) {
      ElPropertyDeploy elProp = desc.elPropertyDeploy(propertyName);
      if (elProp != null && elProp.containsMany()) {
        return SplitName.begin(propName);
      }
    }
    return null;
  }

  @Override
  public void containsMany(BeanDescriptor<?> desc, ManyWhereJoins manyWhereJoin) {
    propertyContainsMany(propName, desc, manyWhereJoin);
  }

  /**
   * Check the logical property path for containing a 'many' property.
   */
  protected void propertyContainsMany(String propertyName, BeanDescriptor<?> desc, ManyWhereJoins manyWhereJoin) {
    if (propertyName != null) {
      ElPropertyDeploy elProp = desc.elPropertyDeploy(propertyName);
      if (elProp != null) {
        if (elProp.containsFormulaWithJoin()) {
          // for findCount query select clause
          manyWhereJoin.addFormulaWithJoin(elProp.elPrefix(), elProp.name());
        }
        if (elProp.containsMany()) {
          // for findCount we join to a many property
          manyWhereJoin.add(elProp);
          if (elProp.isAggregation()) {
            manyWhereJoin.setAggregation();
          }
        }
      }
    }
  }

  @Override
  public void prepareExpression(BeanQueryRequest<?> request) {
    // do nothing
  }

  @Override
  public void validate(SpiExpressionValidation validation) {
    validation.validate(propName, null);
  }

  protected final ElPropertyValue getElProp(SpiExpressionBind request) {
    return request.descriptor().elGetValue(propName);
  }
}
