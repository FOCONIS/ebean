package org.tests.model.docstore;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for beans that need to know it's parent.
 * @author Roland Praml, FOCONIS AG
 *
 */
public interface OwnerBeanAware {

    void setOwnerBeanInfo(Object parent, String propertyName, Object additionalKey);

    public static void postJsonGet(Object bean, Object obj, String fieldName) {
      if (obj instanceof OwnerBeanAware) {
        ((OwnerBeanAware) obj).setOwnerBeanInfo(bean, fieldName, null);
      } else if (obj instanceof Collection) {
        int i = 0;
        for (Object el:(Collection)obj) {
          if (el != null) {
            if (el instanceof OwnerBeanAware) {
              ((OwnerBeanAware) el).setOwnerBeanInfo(bean, fieldName, i++);
            } else {
              return;
            }
          }
        }
      } else if (obj instanceof Map) {
        int i = 0;
        for (Map.Entry<?,?> el:((Map<?,?>)obj).entrySet()) {
          if (el != null) {
            if (el.getValue() instanceof OwnerBeanAware) {
              ((OwnerBeanAware) el.getValue() ).setOwnerBeanInfo(bean, fieldName, el.getKey());
            } else {
              return;
            }
          }
        }
      }
    }
}
