package org.tests.model.tevent;

import io.ebean.annotation.FormulaAlias;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.plugin.DeployBeanDescriptorMeta;
import io.ebean.plugin.DeployBeanPropertyMeta;
import io.ebean.plugin.FormulaComputation;
import io.ebeaninternal.server.deploy.meta.DeployBeanPropertyAssocMany;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Jonas P&ouml;hler, FOCONIS AG
 */
public class CountFormulaComputation implements FormulaComputation {

  @Target(FIELD)
  @Retention(RUNTIME)
  @FormulaAlias(CountFormulaComputation.class)
  public @interface Count {

    String value();

  }


  @Override
  public void compute(final Annotation annotation, final DeployBeanDescriptorMeta descriptor, final DeployBeanPropertyMeta prop,
                      final DatabasePlatform platform) {
    if (!Count.class.isAssignableFrom(annotation.annotationType())) {
      throw new IllegalStateException("Annotation is not @FormulaAlias");
    }
    Count countAnnot = (Count) annotation;
    // @Count found, so build the (complex) count formula
    DeployBeanPropertyAssocMany<?> countProp = (DeployBeanPropertyAssocMany<?>) descriptor.getBeanProperty(countAnnot.value());

    String tmpTable = "f_" + prop.getName();
    String sqlSelect = "coalesce(" + tmpTable + ".child_count, 0)";
    String parentId = countProp.getMappedBy() + "_id";
    String tableName = countProp.getBeanTable().getBaseTable();
    String sqlJoin = "left join (select " + parentId + ", count(*) as child_count from " + tableName + " GROUP BY " + parentId + " )"
      + " " + tmpTable + " on " + tmpTable + "." + parentId + " = ${ta}." + descriptor.idProperty().getDbColumn();
    prop.setSqlFormula(sqlSelect, sqlJoin);
  }

}
