package io.ebeaninternal.server.expression;

import io.ebean.ExpressionListBuilder;
import io.ebean.search.TextSimple;

import java.io.IOException;

/**
 * Full text Multi-Match expression.
 */
class TextSimpleExpression extends AbstractTextExpression {

  private final String search;

  private final TextSimple options;

  public TextSimpleExpression(String search, TextSimple options) {
    super(null);
    this.search = search;
    this.options = options;
  }

  @Override
  public void writeDocQuery(DocQueryContext context) throws IOException {
    context.writeTextSimple(search, options);
  }

  @Override
  protected ExpressionListBuilder.DocStore<?> exprApply(ExpressionListBuilder.DocStore<?> builder) {
    return builder.textSimple(search, options);
  }
}
