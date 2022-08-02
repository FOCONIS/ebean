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
import io.ebeaninternal.server.deploy.meta.*;
import io.ebeaninternal.server.properties.BeanPropertyGetter;
import io.ebeaninternal.server.properties.BeanPropertySetter;
import io.ebeaninternal.server.query.SqlJoinType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class VirtualPropCustomDeployParser implements CustomDeployParser {

  private Set<DeployBeanProperty> virtualProperties = new HashSet<>();
  @Override
  public void parse(DeployBeanDescriptorMeta meta, DatabasePlatform databasePlatform) {

    virtualProperties.forEach(p-> {
      if (p instanceof DeployBeanPropertyAssocMany) {
        p.setGetter(new VirtualGetterMany(p.getPropertyIndex()));
        p.setSetter(new VirtualSetter(p.getPropertyIndex()));
      } else {
        p.setSetter(new VirtualSetter(p.getPropertyIndex()));
        p.setGetter(new VirtualGetter(p.getPropertyIndex()));
      }
    });
  }

  @Override
  public void prepare(DeployBeanDescriptorMeta meta, DatabasePlatform databasePlatform) {
     DeployBeanDescriptor<?> currentDesc = (DeployBeanDescriptor) meta;
    handleOneToOne(currentDesc);

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

      DeployBeanDescriptor parentDescriptor = currentDesc.getDeploy(ann.value()).getDescriptor();
      for (DeployBeanProperty p:currentDesc.properties()) {

        int virtualIndex = addVirtualProperty(parentDescriptor.getBeanType(), p.getName());
        virtualProperties.add(p);
       /* p.setPropertyIndex(virtualIndex);
        if (p instanceof DeployBeanPropertyAssocMany) {
          p.setGetter(new VirtualGetterMany(virtualIndex));
          p.setSetter(new VirtualSetter(virtualIndex));
        } else {
          p.setSetter(new VirtualSetter(virtualIndex));
          p.setGetter(new VirtualGetter(virtualIndex));
        }*/
        p.setDesc(parentDescriptor);
        parentDescriptor.addBeanProperty(p);
      }
      currentDesc.properties().clear();
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
