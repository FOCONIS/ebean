package io.ebeaninternal.server.cache;

import io.ebean.ModifyAwareType;
import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.deploy.BeanProperty;
import io.ebeaninternal.server.deploy.BeanPropertyAssocMany;

import java.util.LinkedHashMap;
import java.util.Map;

public final class CachedBeanDataFromBean {

  public static CachedBeanData extract(BeanDescriptor<?> desc, EntityBean bean) {
    EntityBeanIntercept ebi = bean._ebean_getIntercept();
    Map<String, Object> data = new LinkedHashMap<>();

    BeanProperty idProperty = desc.idProperty();
    if (idProperty != null) {
      int propertyIndex = idProperty.propertyIndex();
      if (ebi.isLoadedProperty(propertyIndex)) {
        data.put(idProperty.name(), idProperty.getCacheDataValue(bean));
      }
    }

    // extract all the non-many properties
    final boolean dirty = ebi.isDirty();
    for (BeanProperty prop : desc.propertiesNonMany()) {
      if (!dirty) {
        if (ebi.value(prop.propertyIndex()) instanceof ModifyAwareType && ((ModifyAwareType) ebi.value(prop.propertyIndex())).isMarkedDirty()) {
          data.put(prop.name(), prop.getCacheDataValueOrig(ebi));
        } else if (ebi.isLoadedProperty(prop.propertyIndex())) {
          data.put(prop.name(), prop.getCacheDataValue(bean));
        }
      } else if (ebi.isDirtyProperty(prop.propertyIndex())
        || ebi.value(prop.propertyIndex()) instanceof ModifyAwareType && ((ModifyAwareType) ebi.value(prop.propertyIndex())).isMarkedDirty()) {
        data.put(prop.name(), prop.getCacheDataValueOrig(ebi));
      } else if (ebi.isLoadedProperty(prop.propertyIndex())) {
        data.put(prop.name(), prop.getCacheDataValue(bean));
      }
    }

    for (BeanPropertyAssocMany<?> prop : desc.propertiesMany()) {
      if (prop.isElementCollection()) {
        if (ebi.isChangedProperty(prop.propertyIndex())) {
          data.put(prop.name(), prop.getCacheDataValueOrig(ebi));
        } else {
          data.put(prop.name(), prop.getCacheDataValue(bean));
        }
      }
    }

    long version = desc.getVersion(bean);
    EntityBean sharableBean = createSharableBean(desc, bean, ebi);
    return new CachedBeanData(sharableBean, desc.discValue(), data, version);
  }

  private static EntityBean createSharableBean(BeanDescriptor<?> desc, EntityBean bean, EntityBeanIntercept beanEbi) {
    if (!desc.isCacheSharableBeans() || !beanEbi.isFullyLoadedBean()) {
      return null;
    }
    if (beanEbi.isReadOnly()) {
      return bean;
    }

    // create a readOnly sharable instance by copying the data
    EntityBean sharableBean = desc.createEntityBean();
    BeanProperty idProp = desc.idProperty();
    if (idProp != null) {
      Object v = idProp.getValue(bean);
      idProp.setValue(sharableBean, v);
    }
    BeanProperty[] propertiesNonTransient = desc.propertiesNonTransient();
    for (BeanProperty aPropertiesNonTransient : propertiesNonTransient) {
      Object v = aPropertiesNonTransient.getValue(bean);
      aPropertiesNonTransient.setValue(sharableBean, v);
    }
    EntityBeanIntercept intercept = sharableBean._ebean_getIntercept();
    intercept.setReadOnly(true);
    intercept.setLoaded();
    return sharableBean;
  }

}
