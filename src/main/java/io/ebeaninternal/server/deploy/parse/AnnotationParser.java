package io.ebeaninternal.server.deploy.parse;

import java.util.HashMap;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.validation.groups.Default;

import io.ebeaninternal.server.deploy.BeanCascadeInfo;
import io.ebeaninternal.server.deploy.meta.DeployBeanDescriptor;
import io.ebeaninternal.server.deploy.meta.DeployBeanProperty;
import io.ebeaninternal.server.deploy.meta.DeployBeanPropertyAssocOne;

/**
 * Base class for reading deployment annotations.
 */
public abstract class AnnotationParser extends AnnotationBase {

  protected final DeployBeanInfo<?> info;

  protected final DeployBeanDescriptor<?> descriptor;

  protected final Class<?> beanType;

  protected final boolean validationAnnotations;

  public AnnotationParser(DeployBeanInfo<?> info, boolean validationAnnotations) {
    super(info.getUtil());
    this.validationAnnotations = validationAnnotations;
    this.info = info;
    this.beanType = info.getDescriptor().getBeanType();
    this.descriptor = info.getDescriptor();
  }

  /**
   * read the deployment annotations.
   */
  @Override
  public abstract void parse();

  /**
   * Helper method to set cascade types to the CascadeInfo on BeanProperty.
   */
  protected void setCascadeTypes(CascadeType[] cascadeTypes, BeanCascadeInfo cascadeInfo) {
    if (cascadeTypes != null && cascadeTypes.length > 0) {
      cascadeInfo.setTypes(cascadeTypes);
    }
  }

  /**
   * Read an AttributeOverrides if they exist for this embedded bean.
   */
  protected void readEmbeddedAttributeOverrides(DeployBeanPropertyAssocOne<?> prop) {

    Set<AttributeOverride> attrOverrides = getAll(prop, AttributeOverride.class);
    if (!attrOverrides.isEmpty()) {
      HashMap<String, String> propMap = new HashMap<>();
      for (AttributeOverride attrOverride : attrOverrides) {
        propMap.put(attrOverride.name(), attrOverride.column().name());
      }
      prop.getDeployEmbedded().putAll(propMap);
    }

  }

  protected void readColumn(Column columnAnn, DeployBeanProperty prop) {

    if (!isEmpty(columnAnn.name())) {
      String dbColumn = databasePlatform.convertQuotedIdentifiers(columnAnn.name());
      prop.setDbColumn(dbColumn);
    }

    prop.setDbInsertable(columnAnn.insertable());
    prop.setDbUpdateable(columnAnn.updatable());
    prop.setNullable(columnAnn.nullable());
    prop.setUnique(columnAnn.unique());
    if (columnAnn.precision() > 0) {
      prop.setDbLength(columnAnn.precision());
    } else if (columnAnn.length() != 255) {
      // set default 255 on DbTypeMap
      prop.setDbLength(columnAnn.length());
    }
    prop.setDbScale(columnAnn.scale());
    prop.setDbColumnDefn(columnAnn.columnDefinition());

    String baseTable = descriptor.getBaseTable();
    String tableName = columnAnn.table();
    if (!"".equals(tableName)) { // if tableName is set without schema (e.g. "o_customer"), prepend schema of baseTable
      int pos = baseTable.indexOf('.');
      if (pos != -1 && tableName.indexOf('.') == -1) {
        tableName = baseTable.substring(0, pos+1) + tableName;
      }
      if (!tableName.equalsIgnoreCase(baseTable)) {
        // its on a secondary table...
        prop.setSecondaryTable(tableName);
      }
    }
  }

  /**
   * Return true if the validation group is {@link Default} or empty.
   */
  protected boolean isEbeanValidationGroups(Class<?>[] groups) {
    return (groups.length == 0
        || groups.length == 1 && javax.validation.groups.Default.class.isAssignableFrom(groups[0]));
  }
}
