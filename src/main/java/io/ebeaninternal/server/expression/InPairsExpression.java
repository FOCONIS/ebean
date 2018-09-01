package io.ebeaninternal.server.expression;

import io.ebean.Pairs;
import io.ebean.event.BeanQueryRequest;
import io.ebeaninternal.api.NaturalKeyQueryData;
import io.ebeaninternal.api.SpiExpression;
import io.ebeaninternal.api.SpiExpressionRequest;
import io.ebeaninternal.server.persist.MultiValueWrapper;
import io.ebeaninternal.server.persist.platform.MultiValueBind;
import io.ebeaninternal.server.persist.platform.MultiValueBind.IsSupported;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class InPairsExpression extends AbstractExpression {

  private final boolean not;

  private final Pairs pairs;

  private final String property0, property1;

  private final List<Pairs.Entry> entries;

  private IsSupported multiValueSupported;

  private final String separator;

  private final String suffix;

  private List<Object> concatBindValues;

  InPairsExpression(Pairs pairs, boolean not) {
    super(pairs.getProperty0());
    this.pairs = pairs;
    this.property0 = pairs.getProperty0();
    this.property1 = pairs.getProperty1();
    this.entries = pairs.getEntries();
    this.not = not;
    this.separator = pairs.getConcatSeparator();
    this.suffix = pairs.getConcatSuffix();
  }

  @Override
  public boolean naturalKey(NaturalKeyQueryData<?> data) {
    return !not && data.matchInPairs(pairs);
  }

  @Override
  public void prepareExpression(BeanQueryRequest<?> request) {

    // at this stage translating pairs into varchar via DB concat
    multiValueSupported = request.isMultiValueSupported(String.class);
  }

  @Override
  public void writeDocQuery(DocQueryContext context) throws IOException {
    throw new RuntimeException("Not supported with document query");
  }

  @Override
  public void addBindValues(SpiExpressionRequest request) {

    // Note at this point entries may have been removed when used with l2 caching
    // ... for each l2 cache hit an entry was removed
    this.concatBindValues = new ArrayList<>(entries.size());
    for (Pairs.Entry entry : entries) {
      concatBindValues.add(concat(entry.getA(), entry.getB()));
    }
    request.addBindValue(new MultiValueWrapper(concatBindValues, String.class));
  }

  /**
   * Using DB concat at this stage. Usually a DB expression index should match the concat.
   */
  private String concat(Object key, Object value) {
    StringBuilder sb = new StringBuilder(30);
    sb.append(key);
    sb.append(separator);
    sb.append(value);
    if (suffix != null) {
      sb.append(suffix);
    }
    return sb.toString();
  }

  @Override
  public void addSql(SpiExpressionRequest request) {

    if (entries.isEmpty()) {
      String expr = not ? "1=1" : "1=0";
      request.append(expr);
      return;
    }


    StringBuilder sb = new StringBuilder();

    sb.append("CONCAT(").append(property0).append(",'").append(separator).append("',").append(property1);

    if (suffix != null && !suffix.isEmpty()) {
      sb.append(",'").append(suffix).append('\'');
    }
    sb.append(')');
    request.append(sb.toString());
    request.appendInExpression(not, concatBindValues);
  }

  /**
   * Based on the number of values in the in clause.
   */
  @Override
  public void queryPlanHash(StringBuilder builder) {
    if (not) {
      builder.append("NotInPairs[");
    } else {
      builder.append("InPairs[");
    }
    builder.append(property0).append("-");
    builder.append(property1).append("-");
    builder.append(separator).append("-");
    builder.append(suffix).append(" ?");
    // query plan specific to the number of parameters in the IN clause
    if (multiValueSupported == IsSupported.NO) {
      builder.append(entries.size());
    } else if (multiValueSupported == IsSupported.ONLY_FOR_MANY_PARAMS) {
      if (entries.size() <= MultiValueBind.MANY_PARAMS) {
        builder.append(entries.size());
      }
    }
    builder.append("]");
  }

  @Override
  public int queryBindHash() {
    int hc = 92821;
    for (Pairs.Entry entry : entries) {
      hc = 92821 * hc + entry.hashCode();
    }
    return hc;
  }

  @Override
  public boolean isSameByBind(SpiExpression other) {

    InPairsExpression that = (InPairsExpression) other;
    return this.entries.size() == that.entries.size() && entries.equals(that.entries);
  }
}
