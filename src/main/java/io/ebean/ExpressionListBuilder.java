package io.ebean;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import io.ebean.search.Match;
import io.ebean.search.MultiMatch;
import io.ebean.search.TextCommonTerms;
import io.ebean.search.TextQueryString;
import io.ebean.search.TextSimple;

/**
 * Base interface implemented by ExpressionList. Can be used to build one
 * Expression List from an other with
 *
 * <code>ExpressionList.applyTo(ExpressionListBuilder)</code>. It is also
 * useful, when you want to inspect a given ExpressionList and build, for
 * example, your own filter/toString implementation. (E.g. you can transform a
 * given expression back to Eql)
 *
 *
 * <pre>
 * ExpressionList permissionCheck = new
 * QBaseModel().owner.eq(currentUser).query().where(); Query someQuery =
 * DB.find(...) permissionCheck.applyTo(query.where()); // this will effectively
 * add the onwer.eq check to that query.
 *
 * As ExpressionList itself implements ExpressionListBuilder, you are able to
 * apply/concat one ExpressionList with other ExpressionLists.
 *
 */
public interface ExpressionListBuilder<T> {

  /**
   * Extension of ExpressionListBuilder for JSON methods.
   */
  public interface Json<T> extends ExpressionListBuilder<T> {

    /**
     * @see ExpressionList#jsonExists(String, String)
     */
    ExpressionListBuilder.Json<T> jsonExists(String propertyName, String path);

    /**
     * @see ExpressionList#jsonNotExists(String, String)
     */
    ExpressionListBuilder.Json<T> jsonNotExists(String propertyName, String path);

    /**
     * @see ExpressionList#jsonEqualTo(String, String, Object)
     */
    ExpressionListBuilder.Json<T> jsonEqualTo(String propertyName, String path, Object value);

    /**
     * @see ExpressionList#jsonNotEqualTo(String, String, Object)
     */
    ExpressionListBuilder.Json<T> jsonNotEqualTo(String propertyName, String path, Object value);

    /**
     * @see ExpressionList#jsonGreaterThan(String, String, Object)
     */
    ExpressionListBuilder.Json<T> jsonGreaterThan(String propertyName, String path, Object value);

    /**
     * @see ExpressionList#jsonGreaterOrEqual(String, String, Object)
     */
    ExpressionListBuilder.Json<T> jsonGreaterOrEqual(String propertyName, String path, Object value);

    /**
     * @see ExpressionList#jsonLessThan(String, String, Object)
     */
    ExpressionListBuilder.Json<T> jsonLessThan(String propertyName, String path, Object value);

    /**
     * @see ExpressionList#jsonLessOrEqualTo(String, String, Object)
     */
    ExpressionListBuilder.Json<T> jsonLessOrEqualTo(String propertyName, String path, Object value);

    /**
     * @see ExpressionList#jsonBetween(String, String, Object, Object)
     */
    ExpressionListBuilder.Json<T> jsonBetween(String propertyName, String path, Object lowerValue, Object upperValue);

  }

  /**
   *  * Extension of ExpressionListBuilder for Postgres-ARRAY support.
   *
   */
  public interface Array<T> extends ExpressionListBuilder<T> {

    /**
     * @see ExpressionList#arrayContains(String, Object...)
     */
    ExpressionListBuilder.Array<T> arrayContains(String propertyName, Object... values);

    /**
     * @see ExpressionList#arrayNotContains(String, Object...)
     */
    ExpressionListBuilder.Array<T> arrayNotContains(String propertyName, Object... values);

    /**
     * @see ExpressionList#arrayIsEmpty(String)
     */
    ExpressionListBuilder.Array<T> arrayIsEmpty(String propertyName);

    /**
     * @see ExpressionList#arrayIsNotEmpty(String)
     */
    ExpressionListBuilder.Array<T> arrayIsNotEmpty(String propertyName);

  }


  /**
   * Extension of ExpressionListBuilder for DocStore methods.
   */
  public interface DocStore<T> extends ExpressionListBuilder<T> {

    /**
     * @see ExpressionList#textSimple(String, TextSimple)
     */
    ExpressionListBuilder.DocStore<T> textSimple(String search, TextSimple options);

    /**
     * @see ExpressionList#textQueryString(String, TextQueryString)
     */
    ExpressionListBuilder.DocStore<T> textQueryString(String search, TextQueryString options);

    /**
     * @see ExpressionList#textCommonTerms(String, TextCommonTerms)
     */
    ExpressionListBuilder.DocStore<T> textCommonTerms(String search, TextCommonTerms options);

    /**
     * @see ExpressionList#match(String, String, Match)
     */
    ExpressionListBuilder.DocStore<T> match(String propertyName, String search, Match options);

    /**
     * @see ExpressionList#multiMatch(String, MultiMatch)
     */
    ExpressionListBuilder.DocStore<T> multiMatch(String search, MultiMatch options);

  }
  /**
   * @see ExpressionList#eq(String, Object)
   */
  ExpressionListBuilder<T> eq(String prop, Object value);

  /**
   * @see ExpressionList#ne(String, Object)
   */
  ExpressionListBuilder<T> ne(String propertyName, Object value);

  /**
   * @see ExpressionList#ine(String, String)
   */
  ExpressionListBuilder<T> ine(String propertyName, String value);

  /**
   * @see ExpressionList#ieq(String, String)
   */
  ExpressionListBuilder<T> ieq(String propertyName, String value);

  /**
   * @see ExpressionList#between(String, Object, Object)
   */
  default ExpressionListBuilder<T> between(String propertyName, Object value1, Object value2) {
    // value1 <= prop && prop <= value2
    return and().ge(propertyName, value1).le(propertyName, value2).endAnd();
  }

  /**
   * @see ExpressionList#betweenProperties(String, String, Object)
   */
  default ExpressionListBuilder<T> betweenProperties(String lowProperty, String highProperty, Object value) {
    // lowProperty <= value && value <= highProperty
    return and().le(lowProperty, value).ge(highProperty, value).endAnd();
  }

  /**
   * @see ExpressionList#inRange(String, Object, Object)
   */
  default ExpressionListBuilder<T> inRange(String propertyName, Object value1, Object value2) {
    return and().ge(propertyName, value1).lt(propertyName, value2).endAnd();
  }

  /**
   * @see ExpressionList#gt(String, Object)
   */
  ExpressionListBuilder<T> gt(String propertyName, Object value);

  /**
   * @see ExpressionList#ge(String, Object)
   */
  ExpressionListBuilder<T> ge(String propertyName, Object value);

  /**
   * @see ExpressionList#lt(String, Object)
   */
  ExpressionListBuilder<T> lt(String propertyName, Object value);

  /**
   * @see ExpressionList#le(String, Object)
   */
  ExpressionListBuilder<T> le(String propertyName, Object value);

  /**
   * @see ExpressionList#isNull(String)
   */
  ExpressionListBuilder<T> isNull(String propertyName);

  /**
   * @see ExpressionList#isNotNull(String)
   */
  ExpressionListBuilder<T> isNotNull(String propertyName);

  /**
   * @see ExpressionList#startsWith(String, String)
   */
  ExpressionListBuilder<T> startsWith(String propertyName, String value);

  /**
   * @see ExpressionList#istartsWith(String, String)
   */
  ExpressionListBuilder<T> istartsWith(String propertyName, String value);

  /**
   * @see ExpressionList#endsWith(String, String)
   */
  ExpressionListBuilder<T> endsWith(String propertyName, String value);

  /**
   * @see ExpressionList#iendsWith(String, String)
   */
  ExpressionListBuilder<T> iendsWith(String propertyName, String value);

  /**
   * @see ExpressionList#contains(String, String)
   */
  ExpressionListBuilder<T> contains(String propertyName, String value);

  /**
   * @see ExpressionList#icontains(String, String)
   */
  ExpressionListBuilder<T> icontains(String propertyName, String value);

  /**
   * @see ExpressionList#allEq(Map)
   */
  default ExpressionListBuilder<T> allEq(Map<String, Object> propertyMap) {
    if (propertyMap.isEmpty()) {
      return this;
    }

    ExpressionListBuilder<T> builder = and();
    for (Entry<String, Object> entry : propertyMap.entrySet()) {
      builder = builder.eq(entry.getKey(), entry.getValue());
    }
    return builder.endAnd();
  }

  /**
   * @see ExpressionList#in(String, Query)
   */
  ExpressionListBuilder<T> in(String propertyName, Query<?> subQuery);

  /**
   * @see ExpressionList#in(String, Collection)
   */
  ExpressionListBuilder<T> in(String propertyName, Collection<?> values);

  /**
   * @see ExpressionList#inOrEmpty(String, Collection)
   */
  default ExpressionListBuilder<T> inOrEmpty(String propertyName, Collection<?> values) {
    if (values == null || values.isEmpty()) {
      return this;
    } else {
      return in(propertyName, values);
    }
  }

  /**
   * @see ExpressionList#notIn(String, Collection)
   */
  ExpressionListBuilder<T> notIn(String propertyName, Collection<?> values);

  /**
   * @see ExpressionList#notIn(String, Query)
   */
  ExpressionListBuilder<T> notIn(String propertyName, Query<?> subQuery);

  /**
   * @see ExpressionList#like(String, String)
   */
  ExpressionListBuilder<T> like(String propertyName, String value);

  /**
   * @see ExpressionList#ilike(String, String)
   */
  ExpressionListBuilder<T> ilike(String propertyName, String value);

  /**
   * @see ExpressionList#bitwiseAny(String, long)
   */
  ExpressionListBuilder<T> bitwiseAny(String propertyName, long flags);

  /**
   * @see ExpressionList#bitwiseAll(String, long)
   */
  ExpressionListBuilder<T> bitwiseAll(String propertyName, long flags);

  /**
   * @see ExpressionList#bitwiseAnd(String, long, long)
   */
  ExpressionListBuilder<T> bitwiseAnd(String propertyName, long flags, long match);

  /**
   * @see ExpressionList#raw(String, Object...)
   */
  ExpressionListBuilder<T> raw(String raw, Object... values);

  /**
   * @see ExpressionList#exists(Query)
   */
  ExpressionListBuilder<T> exists(Query<?> subQuery);

  /**
   * @see ExpressionList#notExists(Query)
   */
  ExpressionListBuilder<T> notExists(Query<?> subQuery);

  /**
   * @see ExpressionList#idIn(Collection)
   */
  ExpressionListBuilder<T> idIn(Collection<?> idValues);

  /**
   * @see ExpressionList#idEq(Object)
   */
  ExpressionListBuilder<T> idEq(Object value);

  /**
   * @see ExpressionList#inPairs(Pairs)
   */
  default ExpressionListBuilder<T> inPairs(Pairs pairs) {
    if (pairs.getEntries().isEmpty()) {
      return this;
    }
    // emulate "inPairs"
    ExpressionListBuilder<T> builder = or();
    for (Pairs.Entry entry : pairs.getEntries()) {
      builder = builder
          .and()
          .eq(pairs.getProperty0(), entry.getA())
          .eq(pairs.getProperty1(), entry.getB())
          .endAnd();
    }
    return builder.endOr();
  }

  /**
   * @see ExpressionList#isEmpty(String)
   */
  ExpressionListBuilder<T> isEmpty(String propertyName);

  /**
   * @see ExpressionList#isNotEmpty(String)
   */
  ExpressionListBuilder<T> isNotEmpty(String propertyName);

  /**
   * @see ExpressionList#and()
   */
  ExpressionListBuilder<T> and();

  /**
   * @see ExpressionList#endAnd()
   */
  ExpressionListBuilder<T> endAnd();

  /**
   * @see ExpressionList#or()
   */
  ExpressionListBuilder<T> or();

  /**
   * @see ExpressionList#endOr()
   */
  ExpressionListBuilder<T> endOr();

  /**
   * @see ExpressionList#not()
   */
  ExpressionListBuilder<T> not();

  /**
   * @see ExpressionList#endNot()
   */
  ExpressionListBuilder<T> endNot();

}
