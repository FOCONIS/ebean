package org.tests.model.virtualprop;

import io.ebean.bean.BeanCollection;
import io.ebean.bean.EntityBean;
import io.ebean.bean.InterceptReadWrite;
import io.ebean.common.BeanList;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.plugin.CustomDeployParser;
import io.ebean.plugin.DeployBeanDescriptorMeta;
import io.ebean.util.AnnotationUtil;
import io.ebeaninternal.server.deploy.BeanDescriptorManager;
import io.ebeaninternal.server.deploy.ManyType;
import io.ebeaninternal.server.deploy.meta.DeployBeanDescriptor;
import io.ebeaninternal.server.deploy.meta.DeployBeanProperty;
import io.ebeaninternal.server.deploy.meta.DeployBeanPropertyAssocMany;
import io.ebeaninternal.server.deploy.meta.DeployBeanPropertyAssocOne;
import io.ebeaninternal.server.properties.BeanPropertyGetter;
import io.ebeaninternal.server.properties.BeanPropertySetter;
import io.ebeaninternal.server.query.SqlJoinType;

import java.lang.reflect.Field;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class VirtualPropCustomDeployParser implements CustomDeployParser {

  @Override
  public void parse(DeployBeanDescriptorMeta meta, DatabasePlatform databasePlatform) {

    DeployBeanDescriptor<?> currentDesc = (DeployBeanDescriptor) meta;
    handleOneToOne(currentDesc);

    for (DeployBeanPropertyAssocMany sourceProp : currentDesc.propertiesAssocMany()) {
      if (sourceProp.getField() == null) {
        continue;
      }
      VirtualManyToMany ann = AnnotationUtil.get(sourceProp.getField(), VirtualManyToMany.class);
      if (ann != null) {
        String propName = getPropertyName(ann.propertyName(), currentDesc.getBeanType().getSimpleName(), "s");

        DeployBeanDescriptor parentDescriptor = currentDesc.getDeploy(sourceProp.getPropertyType()).getDescriptor();
        DeployBeanPropertyAssocMany p = new DeployBeanPropertyAssocMany(parentDescriptor, currentDesc.getBeanType(), ManyType.LIST);

        p.setName(propName);
        p.setModifyListenMode(BeanCollection.ModifyListenMode.ALL);
        p.setManyToMany();
//    p.setO();
        // TODO: p.setExtraWhere("${mta}.ref_table_name='database_connection'");
        //p.setDbInsertable(true);
        //p.setDbUpdateable(true);

        p.setMappedBy(sourceProp.getName());
        //p.setNullable(true);
        p.setFetchType(ann.fetch());
        p.getCascadeInfo().setTypes(ann.cascade());

        p.getTableJoin().setType(SqlJoinType.OUTER);

        // set the intersection table
    /*
    DeployTableJoin intJoin = new DeployTableJoin();
    intJoin.setTable("todo_get_from_ann");

    // add the source to intersection join columns
    intJoin.addJoinColumn(new DeployTableJoinColumn("id", "target_uuid"));

    // set the intersection to dest table join columns
    DeployTableJoin destJoin = p.getTableJoin();
    destJoin.addJoinColumn(new DeployTableJoinColumn("id", "function_package_id").reverse());

    intJoin.setType(SqlJoinType.OUTER);

    // reverse join from dest back to intersection
    DeployTableJoin inverseDest = destJoin.createInverse("todo_get_from_ann");
    p.setIntersectionJoin(intJoin);
    p.setInverseJoin(inverseDest);
*/

        int virtualIndex = addVirtualProperty(parentDescriptor.getBeanType(), propName);
        p.setPropertyIndex(virtualIndex);

        BeanDescriptorManager mgr = currentDesc.getManager();
        p.setBeanTable(mgr.beanTable(currentDesc.getBeanType()));

        p.setSetter(new VirtualSetter(virtualIndex));
        p.setGetter(new VirtualGetterMany(virtualIndex));
        parentDescriptor.addBeanProperty(p);
      }
    }
  }

  private String getPropertyName(String ann, String currentDesc, String suffix) {
    String propName = ann;
    if (propName.isEmpty()) {
      String simpleName = currentDesc;
      propName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1) + suffix;
    }
    return propName;
  }

  private void handleOneToOne(DeployBeanDescriptor<?> currentDesc) {
    VirtualEmbed ann = AnnotationUtil.get(currentDesc.getBeanType(), VirtualEmbed.class);
    if (ann != null) {
      String propName = ann.propertyName();
      if (propName.isEmpty()) {
        String simpleName = currentDesc.getBeanType().getSimpleName();
        propName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
      }
      DeployBeanProperty mapProp = findMappedProperty(currentDesc);

      DeployBeanDescriptor parentDescriptor = currentDesc.getDeploy(ann.value()).getDescriptor();
      DeployBeanPropertyAssocOne p = new DeployBeanPropertyAssocOne(parentDescriptor, currentDesc.getBeanType());
      p.setName(propName);
      p.setOneToOne();
      p.setDbInsertable(true);
      p.setDbUpdateable(true);

      p.setMappedBy(mapProp.getName());
      p.setNullable(true);
      p.setFetchType(ann.fetch());
      p.getCascadeInfo().setTypes(ann.cascade());

      p.setOneToOneExported();
      p.setOrphanRemoval();
      p.setJoinType(true);


      int virtualIndex = addVirtualProperty(parentDescriptor.getBeanType(), propName);
      p.setPropertyIndex(virtualIndex);

      BeanDescriptorManager mgr = currentDesc.getManager();
      p.setBeanTable(mgr.beanTable(currentDesc.getBeanType()));

      p.setSetter(new VirtualSetter(virtualIndex));
      p.setGetter(new VirtualGetter(virtualIndex));
      parentDescriptor.addBeanProperty(p);
    }
  }

  private int addVirtualProperty(Class beanClass, String name) {
    try {
      Field field = beanClass.getField("_ebean_props");
      String[] props = (String[]) field.get(null);
      String[] newProps = new String[props.length + 1];
      System.arraycopy(props, 0, newProps, 0, props.length);
      newProps[props.length] = name;
      field.set(null, newProps);

      field = beanClass.getField("_ebean_virtual_prop_count");
      field.set(null, (int) field.get(null) + 1);

      return props.length;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private DeployBeanProperty findMappedProperty(DeployBeanDescriptor<?> dbd) {
    for (DeployBeanProperty prop : dbd.propertiesAll()) {
      if (prop instanceof DeployBeanPropertyAssocOne && ((DeployBeanPropertyAssocOne<?>) prop).isPrimaryKeyJoin()) {
        return prop;
      }
    }
    throw new IllegalStateException("You need a @PrimaryKeyJoin in " + dbd.getBeanType().getSimpleName());
  }

  private static final class VirtualGetter implements BeanPropertyGetter {

    private final int fieldIndex;

    VirtualGetter(int fieldIndex) {
      this.fieldIndex = fieldIndex;
    }

    @Override
    public Object get(EntityBean bean) {
      return bean._ebean_intercept().getValue(fieldIndex);
    }

    @Override
    public Object getIntercept(EntityBean bean) {
      return bean._ebean_intercept().getValueIntercept(fieldIndex);
    }
  }

  private static final class VirtualGetterMany implements BeanPropertyGetter {

    private final int fieldIndex;

    VirtualGetterMany(int fieldIndex) {
      this.fieldIndex = fieldIndex;
    }

    @Override
    public Object get(EntityBean bean) {
      return bean._ebean_intercept().getValue(fieldIndex);
    }

    @Override
    public Object getIntercept(EntityBean bean) {
      Object ret = bean._ebean_intercept().getValueIntercept(fieldIndex);
      if (ret == null) {
        ret = new BeanList();
        ((InterceptReadWrite) bean._ebean_intercept()).setValue(false, fieldIndex, ret, true);
      }
      return ret;
    }
  }

  private static final class VirtualSetter implements BeanPropertySetter {

    private final int fieldIndex;

    VirtualSetter(int fieldIndex) {
      this.fieldIndex = fieldIndex;
    }

    @Override
    public void set(EntityBean bean, Object value) {

      ((InterceptReadWrite) bean._ebean_intercept()).setValue(false, fieldIndex, value, false);
    }

    @Override
    public void setIntercept(EntityBean bean, Object value) {
      ((InterceptReadWrite) bean._ebean_intercept()).setValue(true, fieldIndex, value, false);
    }

  }

}
