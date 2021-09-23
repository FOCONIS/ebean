package org.tests.model.tevent;

import io.ebean.annotation.FormulaAlias;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.plugin.DeployBeanDescriptorMeta;
import io.ebean.plugin.DeployBeanPropertyMeta;
import io.ebean.plugin.FormulaComputation;

import java.lang.annotation.*;
import java.util.List;

/**
 * @author Jonas P&ouml;hler, FOCONIS AG
 */
public class WhenFormulaComputation implements FormulaComputation<WhenFormulaComputation.When> {

  @Target({ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  @Repeatable(When.List.class)
  @FormulaAlias(WhenFormulaComputation.class)
  public @interface When {

    String field();

    String op() default "=";

    String compareValue();

    String then();

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
      When[] value() default {};
    }

  }

  @Override
  public Class<When> supportedAnnotation() {
    return When.class;
  }

  @Override
  public void compute(final List<When> annotations, final DeployBeanDescriptorMeta descriptor, final DeployBeanPropertyMeta prop,
                      final DatabasePlatform platform) {
    assert annotations.size() > 0;
    StringBuilder sb = new StringBuilder();

    sb.append("(CASE");
    for (When when : annotations) {
      sb.append(" WHEN ${ta}.")
        .append(when.field()).append(" ")
        .append(when.op())
        .append(when.compareValue())
        .append(" THEN ").append(when.then());
    }

    sb.append(" ELSE NULL END)");

    prop.setSqlFormula(sb.toString(), "");
  }


}
