package io.ebean;

import java.util.Set;

/**
 * @author Noemi Praml, Foconis Analytics GmbH
 */
public interface ValidationResult {
  Set<String> tooLongProperties();
  Set<String> unknownProperties();
}
