package io.ebean;

import java.util.Collection;
import java.util.Map;

import io.ebean.search.Match;
import io.ebean.search.MultiMatch;
import io.ebean.search.TextCommonTerms;
import io.ebean.search.TextQueryString;
import io.ebean.search.TextSimple;

/**
 * Adapter that can be extended for easier implementation, e.g. if you only want
 * to implement a subset of methods.
 *
 * It can be also a good idea to inherit from this class, that you do not get
 * breaking changes if the ExpressionVisitor API is extended.
 *
 * @author Roland Praml, FOCONIS AG
 *
 */
public class ExpressionVisitorAdapter implements ExpressionVisitor {

  @Override
  public ExpressionVisitor eq(String prop, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor ne(String propertyName, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor ine(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor ieq(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor between(String propertyName, Object value1, Object value2) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor betweenProperties(String lowProperty, String highProperty, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor inRange(String propertyName, Object value1, Object value2) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor gt(String propertyName, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor ge(String propertyName, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor lt(String propertyName, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor le(String propertyName, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor isNull(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor isNotNull(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor startsWith(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor istartsWith(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor endsWith(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor iendsWith(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor contains(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor icontains(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor allEq(Map<String, Object> propertyMap) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor in(String propertyName, Query<?> subQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor in(String propertyName, Collection<?> values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor inOrEmpty(String propertyName, Collection<?> values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor notIn(String propertyName, Collection<?> values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor notIn(String propertyName, Query<?> subQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor like(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor ilike(String propertyName, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor bitwiseAny(String propertyName, long flags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor bitwiseAll(String propertyName, long flags) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor bitwiseAnd(String propertyName, long flags, long match) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor raw(String raw, Object... values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor arrayContains(String propertyName, Object... values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor arrayNotContains(String propertyName, Object... values) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor arrayIsEmpty(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor arrayIsNotEmpty(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor exists(Query<?> subQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor notExists(Query<?> subQuery) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor idIn(Collection<?> idValues) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor idEq(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor inPairs(Pairs pairs) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor isEmpty(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor isNotEmpty(String propertyName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonExists(String propertyName, String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonNotExists(String propertyName, String path) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonEqualTo(String propertyName, String path, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonNotEqualTo(String propertyName, String path, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonGreaterThan(String propertyName, String path, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonGreaterOrEqual(String propertyName, String path, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonLessThan(String propertyName, String path, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonLessOrEqualTo(String propertyName, String path, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor jsonBetween(String propertyName, String path, Object lowerValue, Object upperValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor textSimple(String search, TextSimple options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor textQueryString(String search, TextQueryString options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor textCommonTerms(String search, TextCommonTerms options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor match(String propertyName, String search, Match options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor multiMatch(String search, MultiMatch options) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor and() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor endAnd() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor or() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor endOr() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor not() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExpressionVisitor endNot() {
    throw new UnsupportedOperationException();
  }

}
