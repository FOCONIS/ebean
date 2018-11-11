package io.ebeaninternal.server.el;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import io.ebean.Query;
import io.ebeaninternal.api.filter.Expression3VL;
import io.ebeaninternal.api.filter.ExpressionTest;
import io.ebeaninternal.api.filter.FilterContext;


/**
 * Contains the various ElMatcher implementations.
 */
class ElMatchBuilder {

  /**
   * No bean will match.
   */
  static class False<T> implements ElMatcher<T> {
    @Override
    public Expression3VL isMatch(T bean, FilterContext ctx) {
      return Expression3VL.FALSE;
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append("false");
    }
  }

  /**
   * All beans will match.
   */
  static class True<T> implements ElMatcher<T> {
    @Override
    public Expression3VL isMatch(T bean, FilterContext ctx) {
      return Expression3VL.TRUE;
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append("true");
    }
  }


  static abstract class Base<T,V> implements ElMatcher<T>, ExpressionTest {

    final ElPropertyValue elGetValue;

    public Base(ElPropertyValue elGetValue) {
      this.elGetValue = elGetValue;
    }

    @Override
    public Expression3VL isMatch(T bean, FilterContext ctx) {
      return elGetValue.pathTest(bean, ctx, this);
    }

    @Override
    public Expression3VL test(Object value) {
      return match((V) value) ? Expression3VL.TRUE : Expression3VL.FALSE;
    }

    /**
     * Test the value, if it matches the filter
     */
    abstract boolean match(V value);
  }

  static abstract class BaseValue<T,V> extends Base<T,V> {

    final V testValue;

    public BaseValue(ElPropertyValue elGetValue, V testValue) {
      super(elGetValue);
      this.testValue = testValue;
    }

    String getLiteral() {
      return "UNKNOWN";
    }


    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue.getElName()).append(' ').append(getLiteral()).append(" '").append(testValue).append('\'');
    }
  }

  /**
   * Internal helper class, to append patterns and literals to a regexp.
   */
  static class RegexAppender {

    final StringBuilder pattern;
    final StringBuilder literalBuffer;

    RegexAppender(int size) {
      pattern = new StringBuilder(size);
      literalBuffer = new StringBuilder(size);
    }

    private void flush() {
      if (literalBuffer.length() != 0) {
        String literal = literalBuffer.toString();
        if (literal.indexOf("\\E") == -1) {
          pattern.append("\\Q").append(literal).append("\\E");
        } else {
          pattern.append(Pattern.quote(literal));
        }
        literalBuffer.setLength(0);
      }
    }

    void appendPattern(String value) {
      flush();
      pattern.append(value);
    }

    void appendLiteral(char ch) {
      literalBuffer.append(ch);
    }

    void appendLiteral(String s) {
      literalBuffer.append(s);
    }
    @Override
    public String toString() {
      flush();
      return pattern.toString();
    }
  }

  /**
   * Case insensitive equals.
   */
  static class RegularExpr<T> extends Base<T,String> {

    final Pattern pattern;

    RegularExpr(ElPropertyValue elGetValue, String value, int options) {
      super(elGetValue);
      this.pattern = Pattern.compile(value, options);
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append("regexp(").append(elGetValue.getElName()).append(", '").append(pattern).append("')");
    }

    @Override
    public boolean match(String v) {
      return pattern.matcher(v).matches();
    }

    static <T> RegularExpr<T> contains(ElPropertyValue elGetValue, String contains, int options) {
      RegexAppender regex = new RegexAppender(contains.length()+32);
      regex.appendPattern(".*");
      regex.appendLiteral(contains);
      regex.appendPattern(".*");
      return new RegularExpr<>(elGetValue, regex.toString(), options);
    }

    static <T> RegularExpr<T> begins(ElPropertyValue elGetValue, String begins, int options) {
      RegexAppender regex = new RegexAppender(begins.length()+32);
      regex.appendLiteral(begins);
      regex.appendPattern(".*");
      return new RegularExpr<>(elGetValue, regex.toString(), options);
    }

    static <T> RegularExpr<T> ends(ElPropertyValue elGetValue, String ends, int options) {
      RegexAppender regex = new RegexAppender(ends.length()+32);
      regex.appendPattern(".*");
      regex.appendLiteral(ends);
      return new RegularExpr<>(elGetValue, regex.toString(), options);
    }

    static <T> RegularExpr<T> like(ElPropertyValue elGetValue, String like, int options) {
      RegexAppender regex = new RegexAppender(like.length()+32);
      for (int i = 0; i < like.length(); i++) {
        char ch = like.charAt(i);
        // currently no escaping is done!
        // if (ch == '|') {
        // if (i < like.length()) {
        // i++;
        // ch = like.charAt(i);
        // }
        // regex.appendLiteral(ch);
        // } else
        if (ch == '%') {
          regex.appendPattern(".*");
        } else if (ch == '_') {
          regex.appendPattern(".*");
        } else {
          regex.appendLiteral(ch);
        }
      }
      return new RegularExpr<>(elGetValue, regex.toString(), options);
    }
  }


  static class Ieq<T> extends BaseValue<T, String> {
    Ieq(ElPropertyValue elGetValue, String value) {
      super(elGetValue, value);
    }

    @Override
    public boolean match(String v) {
      return v.equalsIgnoreCase(testValue);
    }

    @Override
    String getLiteral() {
      return "!=~";
    }
  }

  /**
   * Case insensitive starts with matcher.
   */
  static class IStartsWith<T> extends BaseValue<T, String> {

    private final CharMatch charMatch;

    IStartsWith(ElPropertyValue elGetValue, String value) {
      super(elGetValue, value);
      this.charMatch = new CharMatch(value);
    }

    @Override
    public boolean match(String v) {
      return charMatch.startsWith(v);
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append("iStartsWith(").append(elGetValue.getElName()).append(", '").append(testValue).append("')");
    }
  }

  /**
   * Case insensitive ends with matcher.
   */
  static class IEndsWith<T> extends BaseValue<T, String> {

    final CharMatch charMatch;

    IEndsWith(ElPropertyValue elGetValue, String value) {
      super(elGetValue, value);
      this.charMatch = new CharMatch(value);
    }

    @Override
    public boolean match(String v) {
      return charMatch.endsWith(v);
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append("iEndsWith(").append(elGetValue.getElName()).append(", '").append(testValue).append("')");
    }
  }

  static class StartsWith<T> extends BaseValue<T, String> {
    StartsWith(ElPropertyValue elGetValue, String value) {
      super(elGetValue, value);
    }

    @Override
    public boolean match(String v) {
      return v.startsWith(testValue);
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append("startsWith(").append(elGetValue.getElName()).append(", '").append(testValue).append("')");
    }
  }

  static class EndsWith<T> extends BaseValue<T, String> {
    EndsWith(ElPropertyValue elGetValue, String value) {
      super(elGetValue, value);
    }

    @Override
    public boolean match(String v) {
      return v.endsWith(testValue);
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append("endsWith(").append(elGetValue.getElName()).append(", '").append(testValue).append("')");
    }

  }

  static class IsNull<T> extends Base<T, Object> {

    public IsNull(ElPropertyValue elGetValue) {
      super(elGetValue);
    }

    @Override
    public boolean match(Object v) {
      return false;
    }

    @Override
    public Expression3VL testNull() {
      return Expression3VL.TRUE;
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue).append(" is null");
    }
  }

  static class IsNotNull<T> extends Base<T, Object> {

    public IsNotNull(ElPropertyValue elGetValue) {
      super(elGetValue);
    }

    @Override
    public boolean match(Object value) {
      return true;
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue).append(" is not null");
    }
  }

  static class InSet<T, V> extends Base<T, V> {

    final Set<V> set;

    public InSet(ElPropertyValue elGetValue, Set<V> set) {
      super(elGetValue);
      this.set = set;
    }

    @Override
    public boolean match(V value) {
      return set.contains(value);
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue).append(" in ").append(set);
    }

  }

  static class NotInSet<T, V> extends Base<T, V> {
    final Set<V> set;

    public NotInSet(ElPropertyValue elGetValue, Set<V> set) {
      super(elGetValue);
      this.set = set;
    }

    @Override
    public boolean match(V value) {
      return !set.contains(value);
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue).append(" not in ").append(set);
    }
  }

  /**
   * Special case: In-Query. The subquery must not be executed at construction, as this may return the wrong data.
   */
  static class InQuery<T, V> extends Base<T, V> {

    private Query<?> query;

    private class Tester implements ExpressionTest {
      final Set<V> set = new HashSet<>(query.findSingleAttributeList());
      @Override
      public Expression3VL test(Object value) {
        return set.contains(value) ? Expression3VL.TRUE : Expression3VL.FALSE;
      }
    }

    public InQuery(ElPropertyValue elGetValue, Query<?> query) {
      super(elGetValue);
      this.query = query;
    }

    @Override
    public Expression3VL isMatch(T bean, FilterContext ctx) {
      ExpressionTest test = ctx.computeIfAbsent(this, Tester::new);
      return elGetValue.pathTest(bean, ctx, test);
    }

    @Override
    boolean match(V value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue).append(" in [").append(query).append(']');
    }

  }

  /**
   * Equal To.
   */
  static class Eq<T, V> extends BaseValue<T, V> {

    public Eq(ElPropertyValue elGetValue, V value) {
      super(elGetValue, value);
    }

    @Override
    String getLiteral() {
      return "=";
    }

    @Override
    public boolean match(V v) {
      return Objects.equals(v, testValue);
    }

    @Override
    public Expression3VL testNull() {
      return testValue == null ? Expression3VL.TRUE : Expression3VL.UNKNOWN;
    }
  }

  /**
   * Not Equal To.
   */
  static class Ne<T, V> extends BaseValue<T, V>  {

    public Ne(ElPropertyValue elGetValue, V value) {
      super(elGetValue, value);
    }

    @Override
    String getLiteral() {
      return "!=";
    }

    @Override
    public boolean match(V v) {
      return !Objects.equals(testValue, v);
    }

  }


  /**
   * Between.
   */
  static class Between<T,V> extends Base<T,V> {

    final Comparable<V> min;
    final Comparable<V> max;

    Between(ElPropertyValue elGetValue, Comparable<V> min, Comparable<V> max) {
      super(elGetValue);
      this.min = min;
      this.max = max;
    }

    @Override
    public boolean match(V value) {
      return min.compareTo(value) <= 0
          && max.compareTo(value) >= 0;
    }

    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue).append(" between '").append(min).append("' and '").append(max).append('\'');
    }

  }

  /**
   * Greater Than.
   */
  static class Gt<T,V extends Comparable<V>> extends BaseValue<T,V> {
    Gt(ElPropertyValue elGetValue, V testValue) {
      super(elGetValue, testValue);
    }

    @Override
    String getLiteral() {
      return ">";
    }

    @Override
    public boolean match(V value) {
      return value.compareTo(testValue) > 0;
    }
  }

  /**
   * Greater Than or Equal To.
   */
  static class Ge<T,V extends Comparable<V>> extends BaseValue<T,V> {
    Ge(ElPropertyValue elGetValue, V testValue) {
      super(elGetValue, testValue);
    }

    @Override
    String getLiteral() {
      return ">=";
    }

    @Override
    public boolean match(V value) {
      return value.compareTo(testValue) >= 0;
    }
  }

  /**
   * Less Than or Equal To.
   */
  static class Le<T,V extends Comparable<V>> extends BaseValue<T,V> {
    Le(ElPropertyValue elGetValue, V testValue) {
      super(elGetValue, testValue);
    }
@Override
String getLiteral() {
  return "<=";
}
    @Override
    public boolean match(V value) {
      return value.compareTo(testValue) <= 0;
    }
  }

  /**
   * Less Than.
   */
  static class Lt<T,V extends Comparable<V>> extends BaseValue<T,V> {
    Lt(ElPropertyValue elGetValue, V testValue) {
      super(elGetValue, testValue);
    }
@Override
String getLiteral() {
  return "<";
}
    @Override
    public boolean match(V value) {
      return value.compareTo(testValue) < 0;
    }
  }

  /**
   * Bitwise And.
   */
  static class BitAnd<T> extends Base<T, Long> {

    private long flags;
    private boolean eq;
    private long match;

    public BitAnd(ElPropertyValue elGetValue, long flags, boolean eq, long match) {
      super(elGetValue);
      this.flags = flags;
      this.eq = eq;
      this.match = match;
    }

    @Override
    public boolean match(Long v) {
      if (eq) {
        return (v.longValue() & flags) == match;
      } else {
        return (v.longValue() & flags) != match;
      }
    }
    @Override
    public void toString(StringBuilder sb) {
      sb.append(elGetValue).append(" & ").append(flags);
      if (eq) {
        sb.append(" = ");
      } else {
        sb.append(" != ");
      }
      sb.append(match);
    }
  }
}
