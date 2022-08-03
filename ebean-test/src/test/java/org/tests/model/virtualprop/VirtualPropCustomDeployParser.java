package org.tests.model.virtualprop;

import io.ebean.bean.EntityBean;
import io.ebean.bean.ExtensionInfo;
import io.ebean.bean.InterceptReadWrite;
import io.ebean.common.BeanList;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.plugin.CustomDeployParser;
import io.ebean.plugin.DeployBeanDescriptorMeta;
import io.ebean.util.AnnotationUtil;
import io.ebeaninternal.server.deploy.meta.*;
import io.ebeaninternal.server.properties.BeanPropertyGetter;
import io.ebeaninternal.server.properties.BeanPropertySetter;

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
         addVirtualExtension(parentDescriptor.getBeanType(), currentDesc.getBeanType());
      for (DeployBeanProperty p:currentDesc.properties()) {

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

  private void addVirtualExtension(Class targetClass, Class sourceClass) {
    try {
      // append sourceProps to targetProps

      Field targetField = targetClass.getField("_ebean_props");
      Field sourceField = sourceClass.getField("_ebean_props");

      String[] targetProps = (String[]) targetField.get(null);
      String[] sourceProps = (String[]) sourceField.get(null);
      String[] newProps = new String[targetProps.length + sourceProps.length];
      System.arraycopy(targetProps, 0, newProps, 0, targetProps.length);
      System.arraycopy(sourceProps, 0, newProps, targetProps.length, sourceProps.length);


      targetField.set(null, newProps);

      Field field = targetClass.getField("_ebean_extensions");
      ExtensionInfo[] extensions = (ExtensionInfo[]) field.get(null);
      ExtensionInfo[] newExtensionInfo = new ExtensionInfo[extensions.length+1];
      System.arraycopy(extensions, 0, newExtensionInfo, 0, extensions.length);
      newExtensionInfo[extensions.length] = new ExtensionInfo(targetProps.length, sourceProps.length, extensions.length, sourceClass);
      field.set(null, newExtensionInfo);

      field = sourceClass.getField("_extension_id");
      field.set(null, extensions.length);

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


}
