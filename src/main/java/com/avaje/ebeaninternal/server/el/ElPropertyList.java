package com.avaje.ebeaninternal.server.el;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebean.text.StringParser;
import com.avaje.ebeaninternal.api.SpiExpressionRequest;
import com.avaje.ebeaninternal.server.deploy.BeanProperty;
/**
 * An {@link ElPropertyValue} to support EL-expressions like 'wheel[0].place'
 * @author Roland Praml, FOCONIS AG
 *
 */
public class ElPropertyList implements ElPropertyValue {

  private BeanProperty delegate;
  private int index;

  /**
   * The delegate and Array/List offset.
   */
  public ElPropertyList(BeanProperty delegate, int index) {
    super();
    this.delegate = delegate;
    this.index = index;
  }

  public Object pathGet(Object bean) {
    Object arr = delegate.pathGet(bean);
    if (arr == null) {
      return null;
    } else if (arr.getClass().isArray()) {
      return Array.get(arr, index);
    } else if (arr instanceof List<?>) {
      return ((List<?>)arr).get(index);
    } else {
      throw new ClassCastException(arr.getClass() + " in " + getName() + " is not a List or Array");
    }
  }
  public Object pathGetNested(Object bean) {
    Object arr = delegate.pathGet(bean);
    if (arr == null) {
      return null;
    } else if (arr.getClass().isArray()) {
      return Array.get(arr, index);
    } else if (arr instanceof List<?>) {
      return ((List<?>)arr).get(index);
    } else {
      throw new ClassCastException(arr.getClass() + " in " + getName() + " is not a List or Array");
    }
  }

  public String getElName() {
    return delegate.getElName()+"[" + index +"]";
  }

  @SuppressWarnings("unchecked")
  public void pathSet(Object bean, Object value) {
    Object arr = delegate.pathGet(bean);
    if (arr == null) {
      throw new UnsupportedOperationException("Callot set property at " + getName());
    } else if (arr.getClass().isArray()) {
       Array.set(arr, index, value);
    } else if (arr instanceof List<?>) {
       ((List)arr).set(index, value);
    } else {
      throw new ClassCastException(arr.getClass() + " in " + getName() + " is not a List or Array");
    }
  }
  
  public String getAssocIdInValueExpr(int size) {
    return delegate.getAssocIdInValueExpr(size);
  }

  public String getAssocIdInExpr(String prefix) {
    return delegate.getAssocIdInExpr(prefix);
  }

  public boolean containsFormulaWithJoin() {
    return delegate.containsFormulaWithJoin();
  }

  public Object convert(Object value) {
    return delegate.convert(value);
  }

  public boolean containsMany() {
    return delegate.containsMany();
  }

  public String getAssocIsEmpty(SpiExpressionRequest request, String path) {
    return delegate.getAssocIsEmpty(request, path);
  }

  public boolean containsManySince(String sinceProperty) {
    return delegate.containsManySince(sinceProperty);
  }

  public boolean isAssocId() {
    return delegate.isAssocId();
  }

  public StringParser getStringParser() {
    return delegate.getStringParser();
  }

  public boolean isAssocMany() {
    return delegate.isAssocMany();
  }

  public String getElPrefix() {
    return delegate.getElPrefix();
  }

  public Object parseDateTime(long systemTimeMillis) {
    return delegate.parseDateTime(systemTimeMillis);
  }

  public boolean isAssocProperty() {
    return delegate.isAssocProperty();
  }

  public String getElPlaceholder(boolean encrypted) {
    return delegate.getElPlaceholder(encrypted);
  }

  public boolean isDateTimeCapable() {
    return delegate.isDateTimeCapable();
  }

  public boolean isLocalEncrypted() {
    return delegate.isLocalEncrypted();
  }

  public boolean isDbEncrypted() {
    return delegate.isDbEncrypted();
  }

  public int getJdbcType() {
    return delegate.getJdbcType();
  }

  public String getName() {
    return delegate.getName();
  }


  public String getDbColumn() {
    return delegate.getDbColumn();
  }

  public String getAssocIdExpression(String propName, String bindOperator) {
    return delegate.getAssocIdExpression(propName, bindOperator);
  }

  public BeanProperty getBeanProperty() {
    return delegate.getBeanProperty();
  }

  public Object[] getAssocIdValues(EntityBean bean) {
    return delegate.getAssocIdValues(bean);
  }
}
