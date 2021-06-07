package io.ebean;

import io.ebean.search.Match;
import io.ebean.search.MultiMatch;
import io.ebean.search.TextCommonTerms;
import io.ebean.search.TextQueryString;
import io.ebean.search.TextSimple;

import java.util.Collection;
import java.util.Map;

/**
 * <b>NOTE:</b> This class is basically taken from ebean's Filter and has been slightly modified to match our patterns for conversion.
 * <p>
 * Provides support for filtering and sorting lists of entities without going back to the database.
 * <p>
 * That is, it uses local in-memory sorting and filtering of a list of entity beans. It is not used in a Database query or invoke a Database
 * query.
 * </p>
 * <p>
 * You can optionally specify a sortByClause and if so, the sort will always execute prior to the filter expressions. You can specify any
 * number of filter expressions and they are effectively joined by logical "AND".
 * </p>
 * <p>
 * The result of the filter method will leave the original list unmodified and return a new List instance.
 * </p>
 * <p>
 *
 * <pre>
 * {
 * 	&#64;code
 *
 * 	// get a list of entities (query execution statistics in this case)
 *
 * 	List<MetaQueryStatistic> list =
 * 			DB.find(MetaQueryStatistic.class).findList();
 *
 * 	long nowMinus24Hrs = System.currentTimeMillis() - 24 * (1000 * 60 * 60);
 *
 * 	// sort and filter the list returning a filtered list...
 *
 * 	List<MetaQueryStatistic> filteredList =
 * 			DB.filter(MetaQueryStatistic.class)
 * 					.sort("avgTimeMicros desc")
 * 					.gt("executionCount", 0)
 * 					.gt("lastQueryTime", nowMinus24Hrs)
 * 					.eq("autoTuned", true)
 * 					.maxRows(10)
 * 					.filter(list);
 *
 * }
 * </pre>
 * <p>
 * The propertyNames can traverse the object graph (e.g. customer.name) by using dot notation. If any point during the object graph
 * traversal to get a property value is null then null is returned.
 * </p>
 * <p>
 *
 * <pre>
 * {@code
 *
 * // examples of property names that
 * // ... will traverse the object graph
 * // ... where customer is a property of our bean
 *
 * customer.name
 * customer.shippingAddress.city
 *
 * }
 * </pre>
 * <p>
 *
 * <pre>
 * {
 * 	&#64;code
 *
 * 	// get a list of entities (query execution statistics)
 *
 * 	List<Order> orders =
 * 			DB.find(Order.class).findList();
 *
 * 	// Apply a filter...
 *
 * 	List<Order> filteredOrders =
 * 			DB.filter(Order.class)
 * 					.startsWith("customer.name", "Rob")
 * 					.eq("customer.shippingAddress.city", "Auckland")
 * 					.filter(orders);
 *
 * }
 * </pre>
 *
 * @param <T> the entity bean type
 */
public interface QueryVisitor<T> {

  /**
   * @see ExpressionList#eq(String, Object)
   */
  QueryVisitor<T> eq(String prop, Object value);

  /**
   * @see ExpressionList#ne(String, Object)
   */
  QueryVisitor<T> ne(String propertyName, Object value);

  /**
   * @see ExpressionList#ine(String, String)
   */
  QueryVisitor<T> ine(String propertyName, String value);

  /**
   * @see ExpressionList#ieq(String, String)
   */
  QueryVisitor<T> ieq(String propertyName, String value);

  /**
   * @see ExpressionList#between(String, Object, Object)
   */
  QueryVisitor<T> between(String propertyName, Object value1, Object value2);

  /**
   * @see ExpressionList#betweenProperties(String, String, Object)
   */
  QueryVisitor<T> betweenProperties(String lowProperty, String highProperty, Object value);

  /**
   * @see ExpressionList#inRange(String, Object, Object)
   */
  QueryVisitor<T> inRange(String propertyName, Object value1, Object value2);

  /**
   * @see ExpressionList#gt(String, Object)
   */
  QueryVisitor<T> gt(String propertyName, Object value);

  /**
   * @see ExpressionList#ge(String, Object)
   */
  QueryVisitor<T> ge(String propertyName, Object value);

  /**
   * @see ExpressionList#lt(String, Object)
   */
  QueryVisitor<T> lt(String propertyName, Object value);

  /**
   * @see ExpressionList#le(String, Object)
   */
  QueryVisitor<T> le(String propertyName, Object value);

  /**
   * @see ExpressionList#isNull(String)
   */
  QueryVisitor<T> isNull(String propertyName);

  /**
   * @see ExpressionList#isNotNull(String)
   */
  QueryVisitor<T> isNotNull(String propertyName);

  /**
   * @see ExpressionList#startsWith(String, String)
   */
  QueryVisitor<T> startsWith(String propertyName, String value);

  /**
   * @see ExpressionList#istartsWith(String, String)
   */
  QueryVisitor<T> istartsWith(String propertyName, String value);

  /**
   * @see ExpressionList#endsWith(String, String)
   */
  QueryVisitor<T> endsWith(String propertyName, String value);

  /**
   * @see ExpressionList#iendsWith(String, String)
   */
  QueryVisitor<T> iendsWith(String propertyName, String value);

  /**
   * @see ExpressionList#contains(String, String)
   */
  QueryVisitor<T> contains(String propertyName, String value);

  /**
   * @see ExpressionList#icontains(String, String)
   */
  QueryVisitor<T> icontains(String propertyName, String value);

  /**
   * @see ExpressionList#allEq(Map)
   */
  QueryVisitor<T> allEq(Map<String, Object> propertyMap);

  /**
   * @see ExpressionList#in(String, Query)
   */
  QueryVisitor<T> in(String propertyName, Query<?> subQuery);

  /**
   * @see ExpressionList#in(String, Collection)
   */
  QueryVisitor<T> in(String propertyName, Collection<?> values);

  /**
   * @see ExpressionList#notIn(String, Collection)
   */
  QueryVisitor<T> notIn(String propertyName, Collection<?> values);

  /**
   * @see ExpressionList#notIn(String, Query)
   */
  QueryVisitor<T> notIn(String propertyName, Query<?> subQuery);

  /**
   * @see ExpressionList#like(String, String)
   */
  QueryVisitor<T> like(String propertyName, String value);

  /**
   * @see ExpressionList#ilike(String, String)
   */
  QueryVisitor<T> ilike(String propertyName, String value);

  /**
   * @see ExpressionList#bitwiseAny(String, long)
   */
  QueryVisitor<T> bitwiseAny(String propertyName, long flags);

  /**
   * @see ExpressionList#bitwiseAll(String, long)
   */
  QueryVisitor<T> bitwiseAll(String propertyName, long flags);

  /**
   * @see ExpressionList#bitwiseAnd(String, long, long)
   */
  QueryVisitor<T> bitwiseAnd(String propertyName, long flags, long match);

  /**
   * @see ExpressionList#raw(String, Object...)
   */
  QueryVisitor<T> raw(String raw, Object... values);

  /**
   * @see ExpressionList#arrayContains(String, Object...)
   */
  QueryVisitor<T> arrayContains(String propertyName, Object... values);

  /**
   * @see ExpressionList#arrayNotContains(String, Object...)
   */
  QueryVisitor<T> arrayNotContains(String propertyName, Object... values);

  /**
   * @see ExpressionList#arrayIsEmpty(String)
   */
  QueryVisitor<T> arrayIsEmpty(String propertyName);

  /**
   * @see ExpressionList#arrayIsNotEmpty(String)
   */
  QueryVisitor<T> arrayIsNotEmpty(String propertyName);

  /**
   * @see ExpressionList#exists(Query)
   */
  QueryVisitor<T> exists(Query<?> subQuery);

  /**
   * @see ExpressionList#notExists(Query)
   */
  QueryVisitor<T> notExists(Query<?> subQuery);

  /**
   * @see ExpressionList#idIn(Collection)
   */
  QueryVisitor<T> idIn(Collection<?> idValues);

  /**
   * @see ExpressionList#idEq(Object)
   */
  QueryVisitor<T> idEq(Object value);

  /**
   * @see ExpressionList#inPairs(Pairs)
   */
  QueryVisitor<T> inPairs(Pairs pairs);

  /**
   * @see ExpressionList#isEmpty(String)
   */
  QueryVisitor<T> isEmpty(String propertyName);

  /**
   * @see ExpressionList#isNotEmpty(String)
   */
  QueryVisitor<T> isNotEmpty(String propertyName);

  /**
   * @see ExpressionList#jsonExists(String, String)
   */
  QueryVisitor<T> jsonExists(String propertyName, String path);

  /**
   * @see ExpressionList#jsonNotExists(String, String)
   */
  QueryVisitor<T> jsonNotExists(String propertyName, String path);

  /**
   * @see ExpressionList#jsonEqualTo(String, String, Object)
   */
  QueryVisitor<T> jsonEqualTo(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonNotEqualTo(String, String, Object)
   */
  QueryVisitor<T> jsonNotEqualTo(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonGreaterThan(String, String, Object)
   */
  QueryVisitor<T> jsonGreaterThan(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonGreaterOrEqual(String, String, Object)
   */
  QueryVisitor<T> jsonGreaterOrEqual(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonLessThan(String, String, Object)
   */
  QueryVisitor<T> jsonLessThan(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonLessOrEqualTo(String, String, Object)
   */
  QueryVisitor<T> jsonLessOrEqualTo(String propertyName, String path, Object value);

  /**
   * @see ExpressionList#jsonBetween(String, String, Object, Object)
   */
  QueryVisitor<T> jsonBetween(String propertyName, String path, Object lowerValue, Object upperValue);

  /**
   * @see ExpressionList#match(String, String, Match)
   */
  QueryVisitor<T> match(String propertyName, String search, Match options);

  /**
   * @see ExpressionList#multiMatch(String, MultiMatch)
   */
  QueryVisitor<T> multiMatch(String search, MultiMatch options);

  /**
   * @see ExpressionList#textSimple(String, TextSimple)
   */
  QueryVisitor<T> textSimple(String search, TextSimple options);

  /**
   * @see ExpressionList#textQueryString(String, TextQueryString)
   */
  QueryVisitor<T> textQueryString(String search, TextQueryString options);

  /**
   * @see ExpressionList#textCommonTerms(String, TextCommonTerms)
   */
  QueryVisitor<T> textCommonTerms(String search, TextCommonTerms options);

  /**
   * @see ExpressionList#and()
   */
  QueryVisitor<T> and();

  /**
   * @see ExpressionList#endAnd()
   */
  QueryVisitor<T> endAnd();

  /**
   * @see ExpressionList#or()
   */
  QueryVisitor<T> or();

  /**
   * @see ExpressionList#endOr()
   */
  QueryVisitor<T> endOr();

  /**
   * @see ExpressionList#not()
   */
  QueryVisitor<T> not();

  /**
   * @see ExpressionList#endNot()
   */
  QueryVisitor<T> endNot();
}
