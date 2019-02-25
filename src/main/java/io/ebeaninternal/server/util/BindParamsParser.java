package io.ebeaninternal.server.util;

import io.ebean.config.EncryptKey;
import io.ebeaninternal.api.BindParams;
import io.ebeaninternal.api.BindParams.OrderedList;
import io.ebeaninternal.api.BindParams.Param;
import io.ebeaninternal.server.deploy.BeanDescriptor;

import javax.persistence.PersistenceException;
import java.util.Collection;

/**
 * Parses the BindParams if they are using named parameters.
 * <p>
 * This is a thread safe implementation.
 * </p>
 */
public class BindParamsParser {

  private static final String ENCRYPTKEY_PREFIX = "encryptkey_";
  private static final String ENCRYPTKEY_GAP = "___";

  private static final int ENCRYPTKEY_PREFIX_LEN = ENCRYPTKEY_PREFIX.length();
  private static final int ENCRYPTKEY_GAP_LEN = ENCRYPTKEY_GAP.length();

  /**
   * Used to parse sql looking for named parameters.
   */
  private static final String quote = "'";

  /**
   * Used to parse sql looking for named parameters.
   */
  private static final String colon = ":";

  private final BindParams params;
  private final String sql;

  private final BeanDescriptor<?> beanDescriptor;

  public static String parse(BindParams params, String sql) {
    return parse(params, sql, null);
  }

  public static String parse(BindParams params, String sql, BeanDescriptor<?> beanDescriptor) {
    return new BindParamsParser(params, sql, beanDescriptor).parseSql();
  }

  private BindParamsParser(BindParams params, String sql, BeanDescriptor<?> beanDescriptor) {
    this.params = params;
    this.sql = sql;
    this.beanDescriptor = beanDescriptor;
  }

  /**
   * Parse the sql changed named parameters to positioned parameters if required.
   * <p>
   * The sql is used when named parameters are used.
   * </p>
   * <p>
   * This is used in most cases of named parameters. The case it is NOT used for is
   * named parameters in a having clause. In this case some of the named parameters
   * could be for a where clause and some for the having clause.
   * </p>
   */
  private String parseSql() {

    if (params.isSameBindHash()) {
      String preparedSql = params.getPreparedSql();
      if (preparedSql != null && !preparedSql.isEmpty()) {
        // the sql has already been parsed and positionedParameters are set in order
        return preparedSql;
      }
    }

    String preparedSql;
    if (params.requiresNamedParamsPrepare()) {
      // convert named parameters into ordered list
      OrderedList orderedList = params.createOrderedList();
      parseNamedParams(orderedList);
      preparedSql = orderedList.getPreparedSql();
    } else {
      preparedSql = sql;
    }
    params.setPreparedSql(preparedSql);
    return preparedSql;
  }

  /**
   * Named parameters need to be parsed and replaced with ?.
   */
  private void parseNamedParams(OrderedList orderedList) {

    parseNamedParams(0, orderedList);
  }

  private void parseNamedParams(int startPos, OrderedList orderedList) {

    if (sql == null) {
      throw new PersistenceException("query does not contain any named bind parameters?");
    }
    if (startPos > sql.length()) {
      return;
    }

    // search for quotes and named params... in order...
    int beginQuotePos = sql.indexOf(quote, startPos);
    int nameParamStart = findNameStart(sql, startPos);
    if (beginQuotePos > 0 && beginQuotePos < nameParamStart) {
      // the quote precedes the named parameter...
      // find and add up to the end quote
      int endQuotePos = sql.indexOf(quote, beginQuotePos + 1);
      String sub = sql.substring(startPos, endQuotePos + 1);
      orderedList.appendSql(sub);

      // start again after the end quote
      parseNamedParams(endQuotePos + 1, orderedList);

    } else {
      if (nameParamStart < 0) {
        // no more params, add the rest
        String sub = sql.substring(startPos, sql.length());
        orderedList.appendSql(sub);

      } else {
        // find the end of the parameter name
        int endOfParam = nameParamStart + 1;
        do {
          char c = sql.charAt(endOfParam);
          if (c != '_' && !Character.isLetterOrDigit(c)) {
            break;
          }
          endOfParam++;
        } while (endOfParam < sql.length());

        // add the named parameter value to bindList
        String paramName = sql.substring(nameParamStart + 1, endOfParam);

        Param param;
        if (paramName.startsWith(ENCRYPTKEY_PREFIX)) {
          param = addEncryptKeyParam(paramName);
        } else {
          param = params.getParameter(paramName);
        }

        if (param == null) {
          String msg = "Bind value is not set or null for [" + paramName + "] in [" + sql + "]";
          throw new PersistenceException(msg);
        }

        // here we check for cast. this means, that the named parameter
        // is surrounded by a "cast( :param as Integer)"
        String placeHolder = "?";
        int castStart = findStartOfCast(sql, nameParamStart);
        if (castStart != -1) {
          int castEnd = findEndOfCast(sql, endOfParam);
          String castStatement = sql.substring(castStart,nameParamStart); // "cast ("
          String asStatement = sql.substring(endOfParam, castEnd);        // "as Decimal(30))"
          param.setCastDataType(extractCastDataType(asStatement));
          placeHolder =  castStatement + "?" + asStatement;
          nameParamStart = castStart;
          endOfParam = castEnd;
        }
        String sub = sql.substring(startPos, nameParamStart);
        orderedList.appendSql(sub);

        // check if inValue is a Collection type...
        Object inValue = param.getInValue();
        if (inValue instanceof Collection<?>) {
          // Chop up Collection parameter into a number
          // of individual parameters and add each one individually
          Collection<?> collection = (Collection<?>) inValue;
          int c = 0;
          for (Object elVal : collection) {
            if (++c > 1) {
              orderedList.appendSql(",");
            }
            orderedList.appendSql(placeHolder);
            BindParams.Param elParam = new BindParams.Param();
            elParam.setInValue(elVal);
            orderedList.add(elParam);
          }

        } else {
          // its a normal scalar value parameter...
          orderedList.add(param);
          orderedList.appendSql(placeHolder);
        }

        // continue on after the end of the parameter
        parseNamedParams(endOfParam, orderedList);
      }
    }
  }


  /**
   * Performs a backtracking on <code>sql</code>, if it ends with "cast(". Ignores whitespace.
   */
  private int findStartOfCast(String sql, int pos) {
    int state = 0;
    while (state <= 4 && pos-- > 0) {
      char ch = sql.charAt(pos);
      if (Character.isWhitespace(ch)) continue;
      if (state == 4 && ch != 'c' && ch != 'C') return -1;
      if (state == 3 && ch != 'a' && ch != 'A') return -1;
      if (state == 2 && ch != 's' && ch != 'S') return -1;
      if (state == 1 && ch != 't' && ch != 'T') return -1;
      if (state == 0 && ch != '(') return -1;
      state++;
    }
    return  pos;
  }

  /**
   * Parses the end of the cast statement
   */
  private int findEndOfCast(String sql, int pos) {
    int openBrackets = 1; // we have on open bracket
    while (openBrackets > 0 && pos < sql.length()) {
      char ch = sql.charAt(pos++);
      if (ch == '(') openBrackets++;
      if (ch == ')') openBrackets--;
    }
    return pos;
  }

  /**
   * parses a cast statement like "as varchar(32))" Statement and returns "varchar(32)" for further analysis.
   */
  private String extractCastDataType(String sql) {
    int pos = 0;
    // remove all leading whitespace
    while (pos < sql.length() && Character.isWhitespace(sql.charAt(pos))) {
      pos++;
    }
    pos += 3; // remove "as "
    if (pos < sql.length() - 1) {
      return sql.substring(pos, sql.length() - 1).trim();
    } else {
      return null;
    }
  }

  /**
   * Find the next named parameter start position (based on colon).
   */
  static int findNameStart(String sql, int startPos) {
    while (true) {
      int colonPos = sql.indexOf(colon, startPos);
      if (colonPos > -1) {
        // validate the next character after the colon (ignore postgres cast)
        char c = sql.charAt(colonPos + 1);
        if (c == '_' || Character.isLetterOrDigit(c)) {
          return colonPos;
        } else {
          startPos = colonPos + 2;
          continue;
        }
      }
      return -1;
    }
  }

  /**
   * Add an encryption key bind parameter.
   */
  private Param addEncryptKeyParam(String keyNamedParam) {

    int pos = keyNamedParam.indexOf(ENCRYPTKEY_GAP, ENCRYPTKEY_PREFIX_LEN);

    String tableName = keyNamedParam.substring(ENCRYPTKEY_PREFIX_LEN, pos);
    String columnName = keyNamedParam.substring(pos + ENCRYPTKEY_GAP_LEN);

    EncryptKey key = beanDescriptor.getEncryptKey(tableName, columnName);
    String strKey = key.getStringValue();

    return params.setEncryptionKey(keyNamedParam, strKey);
  }

}
