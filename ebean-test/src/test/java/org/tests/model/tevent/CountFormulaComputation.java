package org.tests.model.tevent;

import io.ebean.annotation.FormulaAlias;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.plugin.DeployBeanDescriptorMeta;
import io.ebean.plugin.DeployBeanPropertyMeta;
import io.ebean.plugin.FormulaComputation;
import io.ebeaninternal.server.deploy.meta.DeployBeanPropertyAssocMany;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class CountFormulaComputation implements FormulaComputation<CountFormulaComputation.Count> {

  @Target(FIELD)
  @Retention(RUNTIME)
  @FormulaAlias(CountFormulaComputation.class)
  public @interface Count {

    String value();

  }

  @Override
  public Class<Count> supportedAnnotation() {
    return Count.class;
  }

  @Override
  public void compute(final List<Count> annotations, final DeployBeanDescriptorMeta descriptor, final DeployBeanPropertyMeta prop,
                      final DatabasePlatform platform) {
    assert annotations.size() == 1;
    final Count annotation = annotations.get(0);

    // @Count found, so build the (complex) count formula
    DeployBeanPropertyAssocMany<?> countProp = (DeployBeanPropertyAssocMany<?>) descriptor.getBeanProperty(annotation.value());

    String tmpTable = "f_" + prop.getName();
    String sqlSelect = "coalesce(" + tmpTable + ".child_count, 0)";
    String parentId = countProp.getMappedBy() + "_id";
    String tableName = countProp.getBeanTable().getBaseTable();
    String sqlJoin = "left join (select " + parentId + ", count(*) as child_count from " + tableName + " GROUP BY " + parentId + " )"
      + " " + tmpTable + " on " + tmpTable + "." + parentId + " = ${ta}." + descriptor.idProperty().getDbColumn();
    prop.setSqlFormula(sqlSelect, sqlJoin);
  }

}
