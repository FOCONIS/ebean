package io.ebeaninternal.server.properties;

import io.ebean.bean.ExtendableBean;
import io.ebean.bean.ExtensionInfo;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Determines the properties on a given bean.
 */
public final class BeanPropertiesReader {

  private final Map<String, Integer> propertyIndexMap = new HashMap<>();
  private final String[] props;

  public BeanPropertiesReader(Class<?> clazz) {
    this.props = getProperties(clazz);
    for (int i = 0; i < props.length; i++) {
      propertyIndexMap.put(props[i], i);
    }
  }

  public String[] getProperties() {
    return props;
  }

  @Override
  public String toString() {
    return Arrays.toString(props);
  }

  public Integer getPropertyIndex(String property) {
    return propertyIndexMap.get(property);
  }

  private String[] concat(String[] arr1, String[] arr2) {
    String[] ret= new String[arr1.length+ arr2.length];
    System.arraycopy(arr1,0,ret,0,arr1.length);
    System.arraycopy(arr2,0,ret,arr1.length,arr2.length);
    return ret;
  }
  private String[] getProperties(Class<?> clazz) {
    try {
      Field field = clazz.getField("_ebean_props");
      String[] props = (String[]) field.get(null);

      if (ExtendableBean.class.isAssignableFrom(clazz)) {
        field = clazz.getField("_ebean_extensions");
        ExtensionInfo extensions = (ExtensionInfo) field.get(null);
        if (extensions != null) {
          for (ExtensionInfo.Entry extension : extensions) {
            props = concat(props, extension.getProperties());
          }
        }
      }

      return props;
    } catch (Exception e) {
      throw new IllegalStateException("Error getting _ebean_props field on type " + clazz, e);
    }
  }
}
