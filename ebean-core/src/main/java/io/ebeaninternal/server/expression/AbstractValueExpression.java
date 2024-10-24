package io.ebeaninternal.server.expression;

import io.ebeaninternal.api.SpiExpression;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.el.ElPropertyValue;

/**
 * Abstract expression that helps with named parameter use.
 */
abstract class AbstractValueExpression extends AbstractExpression {

  protected final Object bindValue;

  /**
   * Construct with property name and potential named parameter.
   */
  protected AbstractValueExpression(String propName, Object bindValue) {
    super(propName);
    this.bindValue = bindValue;
  }

  /**
   * Return the bind value taking into account named parameters.
   */
  protected Object value() {
    return NamedParamHelp.value(bindValue);
  }

  /**
   * Return the String bind value taking into account named parameters.
   */
  protected String strValue() {
    return NamedParamHelp.valueAsString(bindValue);
  }

  @Override
  public SpiExpression simplify(BeanDescriptor<?> descriptor) {
    ElPropertyValue prop = descriptor.elGetValue(propName);
    if (prop != null && !prop.isRangeValid(value())) {
      return new RawExpression(SQL_FALSE, null);
    }
    return this;
  }

}
