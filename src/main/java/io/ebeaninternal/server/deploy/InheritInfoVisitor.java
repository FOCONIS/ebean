package io.ebeaninternal.server.deploy;

/**
 * Used to visitExpression all the InheritInfo in a single inheritance hierarchy.
 */
public interface InheritInfoVisitor {

  /**
   * visitExpression the InheritInfo for this node.
   */
  void visit(InheritInfo inheritInfo);

}
