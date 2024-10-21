package io.ebeaninternal.api;

import io.ebean.ValidationResult;
import io.ebean.plugin.BeanType;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Property expression validation request for a given root bean type.
 */
public final class SpiExpressionValidation implements ValidationResult {

  private final BeanType<?> desc;
  private final boolean validateLength;
  private final LinkedHashSet<String> unknown = new LinkedHashSet<>();
  private final LinkedHashSet<String> tooLong = new LinkedHashSet<>();


  public SpiExpressionValidation(BeanType<?> desc, boolean validateLength) {
    this.desc = desc;
    this.validateLength = validateLength;
  }

  /**
   * Validate that the property expression (path) is valid.
   */
  public void validate(String propertyName) {
    if (!desc.isValidExpression(propertyName)) {
      unknown.add(propertyName);
    }
  }

  /**
   * Validate that the property expression (path) and value is valid.
   */
  public void validate(String propertyName, Object value) {
    if (!desc.isValidExpression(propertyName)) {
      unknown.add(propertyName);
    } else if (validateLength && value != null && !desc.isValidValue(propertyName, value)) {
      tooLong.add(propertyName);
    }
  }

  /**
   * Return the set of properties considered as having unknown paths.
   */
  public Set<String> unknownProperties() {
    return unknown;
  }

  public Set<String> tooLongProperties() {return tooLong;}

}
