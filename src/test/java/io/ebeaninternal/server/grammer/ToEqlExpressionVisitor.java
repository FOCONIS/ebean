package io.ebeaninternal.server.grammer;

import java.util.Collection;

import io.ebean.ExpressionVisitor;
import io.ebean.ExpressionVisitorAdapter;

/**
 * This is a POC implementation that converts a given ExpressionList back to Eql.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class ToEqlExpressionVisitor extends ExpressionVisitorAdapter {

  private final StringBuilder sb;
  private final String junctionMode;
  private final ExpressionVisitor parent;
  private int count;

  public ToEqlExpressionVisitor() {
    this(new StringBuilder(), " and ", null);
  }

  public ToEqlExpressionVisitor(StringBuilder sb, String junctionMode, ExpressionVisitor parent) {
    this.sb = sb;
    this.junctionMode = junctionMode;
    this.parent = parent;
  }

  @Override
  public ExpressionVisitor between(String prop, Object value1, Object value2) {
    nextExpr();
    prop(prop).append(" between ");
    value(value1).append(" and ");
    value(value2);
    return this;
  }

  @Override
  public ExpressionVisitor inRange(String prop, Object value1, Object value2) {
    nextExpr();
    prop(prop).append(" inRange ");
    value(value1).append(" to ");
    value(value2);
    return this;
  }

  @Override
  public ExpressionVisitor eq(String prop, Object value) {
    nextExpr();
    prop(prop).append(" = ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor ne(String prop, Object value) {
    nextExpr();
    prop(prop).append(" <> ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor ieq(String prop, String value) {
    nextExpr();
    prop(prop).append(" ieq ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor ine(String prop, String value) {
    nextExpr();
    prop(prop).append(" ine ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor gt(String prop, Object value) {
    nextExpr();
    prop(prop).append(" > ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor ge(String prop, Object value) {
    nextExpr();
    prop(prop).append(" >= ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor lt(String prop, Object value) {
    nextExpr();
    prop(prop).append(" < ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor le(String prop, Object value) {
    nextExpr();
    prop(prop).append(" <= ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor betweenProperties(String lowProperty, String highProperty, Object value) {
    nextExpr();
    value(value).append(" between ");
    prop(lowProperty).append(" and ");
    prop(highProperty);
    return this;
  }

  @Override
  public ExpressionVisitor in(String prop, Collection<?> values) {
    nextExpr();
    prop(prop).append(" in (");
    for (Object value : values) {
      value(value).append(',');
    }
    sb.setCharAt(sb.length() - 1, ')');
    return this;
  }

  @Override
  public ExpressionVisitor like(String prop, String value) {
    nextExpr();
    prop(prop).append(" like ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor ilike(String prop, String value) {
    nextExpr();
    prop(prop).append(" ilike ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor startsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" startsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionVisitor endsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" endsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionVisitor contains(String prop, String value) {
    nextExpr();
    prop(prop).append(" contains ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor istartsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" istartsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionVisitor iendsWith(String prop, String value) {
    nextExpr();
    prop(prop).append(" iendsWith ");
    value(value);
    return this;
  }
  @Override
  public ExpressionVisitor icontains(String prop, String value) {
    nextExpr();
    prop(prop).append(" icontains ");
    value(value);
    return this;
  }

  @Override
  public ExpressionVisitor isNull(String prop) {
    nextExpr();
    prop(prop).append(" isNull");
    return this;
  }

  @Override
  public ExpressionVisitor isNotNull(String prop) {
    nextExpr();
    prop(prop).append(" isNotNull");
    return this;
  }

  @Override
  public ExpressionVisitor and() {
    sb.append('(');
    return new ToEqlExpressionVisitor(sb, " and ", this);
  }

  @Override
  public ExpressionVisitor endAnd() {
    assert junctionMode.equals(" and ") : junctionMode;
    sb.append(')');
    return parent;
  }

  @Override
  public ExpressionVisitor or() {
    sb.append('(');
    return new ToEqlExpressionVisitor(sb, " or ", this);
  }

  @Override
  public ExpressionVisitor endOr() {
    assert junctionMode.equals(" or ") : junctionMode;
    sb.append(')');
    return parent;
  }

  @Override
  public ExpressionVisitor not() {
    sb.append("not (");
    return new ToEqlExpressionVisitor(sb, " and ", this);
  }

  @Override
  public ExpressionVisitor endNot() {
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
}
