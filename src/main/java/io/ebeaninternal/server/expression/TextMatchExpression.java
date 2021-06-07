package io.ebeaninternal.server.expression;

import io.ebean.QueryVisitor;
import io.ebean.search.Match;

import java.io.IOException;

/**
 * Full text MATCH expression.
 */
public class TextMatchExpression extends AbstractTextExpression {

  private final String search;

  private final Match options;

  public TextMatchExpression(String propertyName, String search, Match options) {
    super(propertyName);
    this.search = search;
    this.options = options;
  }

  @Override
  public void writeDocQuery(DocQueryContext context) throws IOException {
    context.writeMatch(propName, search, options);
  }

  @Override
  public void visitExpression(final QueryVisitor<?> target) {
    target.match(propName, search, options);
  }
}
