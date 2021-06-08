package io.ebean;

/**
 * An expression that is part of a WHERE or HAVING clause.
 */
public interface Expression {

  /**
   * Visitor method to inspect that expression
   */
  void visit(ExpressionVisitor visitor);
}
