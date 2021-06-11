package io.ebeaninternal.server.grammer;

import java.util.Collection;

import io.ebean.ExpressionListBuilder;
import io.ebean.Query;

/**
 * This is a POC implementation that converts a given ExpressionList back to Eql.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class ToEqlExpressionBuilder<T> implements ExpressionListBuilder<T> {

  private final StringBuilder sb;
  private final String junctionMode;
  private final ExpressionListBuilder<T> parent;
  private int count;

  public ToEqlExpressionBuilder() {
    this(new StringBuilder(), " and ", null);
  }

  public ToEqlExpressionBuilder(StringBuilder sb, String junctionMode, ExpressionListBuilder<T> parent) {
    this.sb = sb;
    this.junctionMode = junctionMode;
    this.parent = parent;
  }

  @Override
  public ExpressionListBuilder<T> between(String prop, Object value1, Object value2) {
    nextExpr();
    prop(prop).append(" between ");
    value(value1).append(" and ");
    value(value2);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> inRange(String prop, Object value1, Object value2) {
    nextExpr();
    prop(prop).append(" inRange ");
    value(value1).append(" to ");
    value(value2);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> eq(String prop, Object value) {
    nextExpr();
    prop(prop).append(" = ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> ne(String prop, Object value) {
    nextExpr();
    prop(prop).append(" <> ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> ieq(String prop, String value) {
    nextExpr();
    prop(prop).append(" ieq ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> ine(String prop, String value) {
    nextExpr();
    prop(prop).append(" ine ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> gt(String prop, Object value) {
    nextExpr();
    prop(prop).append(" > ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> ge(String prop, Object value) {
    nextExpr();
    prop(prop).append(" >= ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> lt(String prop, Object value) {
    nextExpr();
    prop(prop).append(" < ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> le(String prop, Object value) {
    nextExpr();
    prop(prop).append(" <= ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> betweenProperties(String lowProperty, String highProperty, Object value) {
    nextExpr();
    value(value).append(" between ");
    prop(lowProperty).append(" and ");
    prop(highProperty);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> in(String prop, Collection<?> values) {
    nextExpr();
    prop(prop).append(" in (");
    for (Object value : values) {
      value(value).append(',');
    }
    sb.setCharAt(sb.length() - 1, ')');
    return this;
  }

  @Override
  public ExpressionListBuilder<T> like(String prop, String value) {
    nextExpr();
    prop(prop).append(" like ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> ilike(String prop, String value) {
    nextExpr();
    prop(prop).append(" ilike ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> startsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" startsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionListBuilder<T> endsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" endsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionListBuilder<T> contains(String prop, String value) {
    nextExpr();
    prop(prop).append(" contains ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> istartsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" istartsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionListBuilder<T> iendsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" iendsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionListBuilder<T> icontains(String prop, String value) {
    nextExpr();
    prop(prop).append(" icontains ");
    value(value);
    return this;
  }

  @Override
  public ExpressionListBuilder<T> isNull(String prop) {
    nextExpr();
    prop(prop).append(" isNull");
    return this;
  }

  @Override
  public ExpressionListBuilder<T> isNotNull(String prop) {
    nextExpr();
    prop(prop).append(" isNotNull");
    return this;
  }

  @Override
  public ExpressionListBuilder<T> and() {
    sb.append('(');
    return new ToEqlExpressionBuilder(sb, " and ", this);
  }

  @Override
  public ExpressionListBuilder<T> endAnd() {
    assert junctionMode.equals(" and ") : junctionMode;
    sb.append(')');
    return parent;
  }

  @Override
  public ExpressionListBuilder<T> or() {
    sb.append('(');
    return new ToEqlExpressionBuilder(sb, " or ", this);
  }

  @Override
  public ExpressionListBuilder<T> endOr() {
    assert junctionMode.equals(" or ") : junctionMode;
    sb.append(')');
    return parent;
  }

  @Override
  public ExpressionListBuilder<T> not() {
    sb.append("not (");
    return new ToEqlExpressionBuilder<>(sb, " and ", this);
  }

  @Override
  public ExpressionListBuilder<T> endNot() {
    assert junctionMode.equals(" and ") : junctionMode;
    sb.append(')');
    return parent;
  }

  private void nextExpr() {
    if (count++ > 0) {
      sb.append(junctionMode);
    }
  }

  private StringBuilder value(Object value) {
    if (value instanceof String) {
      sb.append('\'').append(value).append('\''); // TODO: how to escape '
    } else {
      sb.append(value);
    }
    return sb;
  }

  private StringBuilder prop(String propertyName) {
    sb.append(propertyName);
    return sb;
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  @Override
  public ExpressionListBuilder<T> notIn(String propertyName, Collection<?> values) {
    return not().in(propertyName, values).endNot();
  }

  @Override
  public ExpressionListBuilder<T> notIn(String propertyName, Query<?> subQuery) {
    return not().in(propertyName, subQuery).endNot();
  }

  @Override
  public ExpressionListBuilder<T> in(String propertyName, Query<?> subQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> bitwiseAny(String propertyName, long flags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> bitwiseAll(String propertyName, long flags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> bitwiseAnd(String propertyName, long flags, long match) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> raw(String raw, Object... values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> exists(Query<?> subQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> notExists(Query<?> subQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> idIn(Collection<?> idValues) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> idEq(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> isEmpty(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionListBuilder<T> isNotEmpty(String propertyName) {
    throw new UnsupportedOperationException();
  }



}
