package io.ebean;

import io.ebean.search.Match;
import io.ebean.search.MultiMatch;
import io.ebean.search.TextCommonTerms;
import io.ebean.search.TextQueryString;
import io.ebean.search.TextSimple;

import java.util.Collection;
import java.util.Map;

/**
 * To inspect a given Expression/ExpressionList call the
 * <code>visit(ExpressionVisitor)</code>.
 *
 * For each expression, the related method will be called. This can be used to
 * write your own in memory filter or to append one ExpressionList to an other,
 * because ExpressionList itself implements ExpressionVisitor.
 *
 * <pre>
 *   ExpressionList permissionCheck = new QBaseModel().owner.eq(currentUser).query().where();
 *   Query someQuery = DB.find(...)
 *   permissionCheck.visit(query.where()); // this will effectively add the onwer.eq check to that query.
 *
 * As ExpressionList itself implements ExpressionVisitor, you
 */
public interface ExpressionVisitor  {

  /**
   * @see ExpressionList#eq(String, Object)
   */
  ExpressionVisitor eq(String prop, Object value);

  /**
   * @see ExpressionList#ne(String, Object)
   */
  ExpressionVisitor ne(String propertyName, Object value);

  /**
   * @see ExpressionList#ine(String, String)
   */
  ExpressionVisitor ine(String propertyName, String value);

  /**
   * @see ExpressionList#ieq(String, String)
   */
  ExpressionVisitor ieq(String propertyName, String value);

  /**
   * @see ExpressionList#between(String, Object, Object)
   */
  ExpressionVisitor between(String propertyName, Object value1, Object value2);

  /**
   * @see ExpressionList#betweenProperties(String, String, Object)
   */
  ExpressionVisitor betweenProperties(String lowProperty, String highProperty, Object value);

  /**
   * @see ExpressionList#inRange(String, Object, Object)
   */
  ExpressionVisitor inRange(String propertyName, Object value1, Object value2);

  /**
   * @see ExpressionList#gt(String, Object)
   */
  ExpressionVisitor gt(String propertyName, Object value);

  /**
   * @see ExpressionList#ge(String, Object)
   */
  ExpressionVisitor ge(String propertyName, Object value);

  /**
   * @see ExpressionList#lt(String, Object)
   */
  ExpressionVisitor lt(String propertyName, Object value);

  /**
   * @see ExpressionList#le(String, Object)
   */
  ExpressionVisitor le(String propertyName, Object value);

  /**
   * @see ExpressionList#isNull(String)
   */
  ExpressionVisitor isNull(String propertyName);

  /**
   * @see ExpressionList#isNotNull(String)
   */
  ExpressionVisitor isNotNull(String propertyName);

  /**
   * @see ExpressionList#startsWith(String, String)
   */
  ExpressionVisitor startsWith(String propertyName, String value);

  /**
   * @see ExpressionList#istartsWith(String, String)
   */
  ExpressionVisitor istartsWith(String propertyName, String value);

  /**
   * @see ExpressionList#endsWith(String, String)
   */
  ExpressionVisitor endsWith(String propertyName, String value);

  /**
   * @see ExpressionList#iendsWith(String, String)
   */
  ExpressionVisitor iendsWith(String propertyName, String value);

  /**
   * @see ExpressionList#contains(String, String)
   */
  ExpressionVisitor contains(String propertyName, String value);

  /**
   * @see ExpressionList#icontains(String, String)
   */
  ExpressionVisitor icontains(String propertyName, String value);

  /**
   * @see ExpressionList#allEq(Map)
   */
  ExpressionVisitor allEq(Map<String, Object> propertyMap);

  /**
   * @see ExpressionList#in(String, Query)
   */
  ExpressionVisitor in(String propertyName, Query<?> subQuery);

  /**
   * @see ExpressionList#in(String, Collection)
   */
  ExpressionVisitor in(String propertyName, Collection<?> values);

  /**
   * @see ExpressionList#inOrEmpty(String, Collection)
   */
  ExpressionVisitor inOrEmpty(String propertyName, Collection<?> values);

  /**
   * @see ExpressionList#notIn(String, Collection)
   */
  ExpressionVisitor notIn(String propertyName, Collection<?> values);

  /**
   * @see ExpressionList#notIn(String, Query)
   */
  ExpressionVisitor notIn(String propertyName, Query<?> subQuery);

  /**
   * @see ExpressionList#like(String, String)
   */
  ExpressionVisitor like(String propertyName, String value);

  /**
   * @see ExpressionList#ilike(String, String)
   */
  ExpressionVisitor ilike(String propertyName, String value);

  /**
   * @see ExpressionList#bitwiseAny(String, long)
   */
  ExpressionVisitor bitwiseAny(String propertyName, long flags);

  /**
   * @see ExpressionList#bitwiseAll(String, long)
   */
  ExpressionVisitor bitwiseAll(String propertyName, long flags);

  /**
   * @see ExpressionList#bitwiseAnd(String, long, long)
   */
  ExpressionVisitor bitwiseAnd(String propertyName, long flags, long match);

  /**
   * @see ExpressionList#raw(String, Object...)
   */
  ExpressionVisitor raw(String raw, Object... values);

  /**
   * @see ExpressionList#arrayContains(String, Object...)
   */
  ExpressionVisitor arrayContains(String propertyName, Object... values);

  /**
   * @see ExpressionList#arrayNotContains(String, Object...)
   */
  ExpressionVisitor arrayNotContains(String propertyName, Object... values);

  /**
   * @see ExpressionList#arrayIsEmpty(String)
   */
  ExpressionVisitor arrayIsEmpty(String propertyName);

  /**
   * @see ExpressionList#arrayIsNotEmpty(String)
   */
  ExpressionVisitor arrayIsNotEmpty(String propertyName);

  /**
   * @see ExpressionList#exists(Query)
   */
  ExpressionVisitor exists(Query<?> subQuery);

  /**
   * @see ExpressionList#notExists(Query)
   */
  ExpressionVisitor notExists(Query<?> subQuery);

  /**
   * @see ExpressionList#idIn(Collection)
   */
  ExpressionVisitor idIn(Collection<?> idValues);

  /**
   * @see ExpressionList#idEq(Object)
   */
  ExpressionVisitor idEq(Object value);

  /**
   * @see ExpressionList#inPairs(Pairs)
   */
  ExpressionVisitor inPairs(Pairs pairs);

  /**
   * @see ExpressionList#isEmpty(String)
   */
  ExpressionVisitor isEmpty(String propertyName);

  /**
   * @see ExpressionList#isNotEmpty(String)
   */
  ExpressionVisitor isNotEmpty(String propertyName);

  /**
   * @see ExpressionList#jsonExists(String, String)
   */
  ExpressionVisitor jsonExists(String propertyName, String path);

  /**
   * @see ExpressionList#jsonNotExists(String, String)
   */
  ExpressionVisitor jsonNotExists(String propertyName, String path);

  /**
   * @see ExpressionList#jsonEqualTo(String, String, Object)
   */
  ExpressionVisitor jsonEqualTo(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonNotEqualTo(String, String, Object)
   */
  ExpressionVisitor jsonNotEqualTo(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonGreaterThan(String, String, Object)
   */
  ExpressionVisitor jsonGreaterThan(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonGreaterOrEqual(String, String, Object)
   */
  ExpressionVisitor jsonGreaterOrEqual(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonLessThan(String, String, Object)
   */
  ExpressionVisitor jsonLessThan(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonLessOrEqualTo(String, String, Object)
   */
  ExpressionVisitor jsonLessOrEqualTo(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonBetween(String, String, Object, Object)
   */
  ExpressionVisitor jsonBetween(String propertyName, String path, Object lowerValue, Object upperValue);

  /**
   * @see ExpressionList#textSimple(String, TextSimple)
   */
  ExpressionVisitor textSimple(String search, TextSimple options);

  /**
   * @see ExpressionList#textQueryString(String, TextQueryString)
   */
  ExpressionVisitor textQueryString(String search, TextQueryString options);

  /**
   * @see ExpressionList#textCommonTerms(String, TextCommonTerms)
   */
  ExpressionVisitor textCommonTerms(String search, TextCommonTerms options);


  /**
   * @see ExpressionList#match(String, String, Match)
   */
  ExpressionVisitor match(String propertyName, String search, Match options);

  /**
   * @see ExpressionList#multiMatch(String, MultiMatch)
   */
  ExpressionVisitor multiMatch(String search, MultiMatch options);

  /**
   * @see ExpressionList#and()
   */
  ExpressionVisitor and();

  /**
   * @see ExpressionList#endAnd()
   */
  ExpressionVisitor endAnd();

  /**
   * @see ExpressionList#or()
   */
  ExpressionVisitor or();

  /**
   * @see ExpressionList#endOr()
   */
  ExpressionVisitor endOr();

  /**
   * @see ExpressionList#not()
   */
  ExpressionVisitor not();

  /**
   * @see ExpressionList#endNot()
   */
  ExpressionVisitor endNot();

}
