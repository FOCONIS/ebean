package io.ebean.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtensionInfo implements Iterable<ExtensionInfo.Entry> {

  private final int startOffset;
  private List<Entry> entries = new ArrayList<>();
  private final ExtensionInfo parent;
  private int propertyLength = -1;

  static <T extends ExtendableBean> ExtensionInfo get(Class<T> clazz) throws ReflectiveOperationException {
    Field field = clazz.getField("_ebean_extensions");
    return (ExtensionInfo) field.get(null);
  }

  public ExtensionInfo(Class clazz, ExtensionInfo parent) {
    try {
      Field targetField = clazz.getField("_ebean_props");
      String[] props = (String[]) targetField.get(null);
      this.startOffset = props.length;
      this.parent = parent;
    } catch (ReflectiveOperationException re) {
      throw new RuntimeException(re);
    }
  }

  public Entry add(String[] props, Class type) throws ReflectiveOperationException {
    if (propertyLength != -1) {
      throw new UnsupportedOperationException("The extension is already in use and cannot be extended anymore");
    }
    int offset = 0;
    if (!entries.isEmpty()) {
      offset = entries.get(entries.size() - 1).offset;
    }
    Entry entry = new Entry(offset, props, entries.size(), type);
    entries.add(entry);
    return entry;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public int getPropertyLength() {
    if (propertyLength == -1) {
      throw new IllegalStateException("Not yet initialized");
    }
    return propertyLength;
  }

  public int size() {
    return entries.size();
  }

  public void init() {
    if (propertyLength == -1) {
      propertyLength = 0;
      for (Entry entry : entries) {
        propertyLength += entry.getLength();
      }
      if (parent != null) {
        parent.init();
        if (entries.isEmpty()) {
          entries = parent.entries;
          propertyLength = parent.propertyLength;
        } else {
          for (Entry entry : parent.entries) {
            entries.add(new Entry(propertyLength, entry.properties, entries.size(), entry.type, entry.prototype));
            propertyLength += entry.getLength();
          }
        }
      }
    }
  }

  public Entry get(int index) {
    return entries.get(index);
  }

  @Override
  public Iterator<Entry> iterator() {
    return entries.iterator();
  }

  public static class Entry {
    private final int offset;
    private final String[] properties;

    private final int index;
    private final EntityBean prototype;
    private final Class type;

    private Entry(int offset, String[] properties, int index, Class type) throws ReflectiveOperationException {
      this.offset = offset;
      this.properties = properties;
      this.index = index;
      this.type = type;
      this.prototype = (EntityBean) type.getConstructor().newInstance();
    }

    private Entry(int offset, String[] properties, int index, Class type, EntityBean prototype) {
      this.offset = offset;
      this.properties = properties;
      this.index = index;
      this.type = type;
      this.prototype = prototype;
    }

    public int getOffset() {
      return offset;
    }

    public int getLength() {
      return properties.length;
    }

    public String[] getProperties() {
      return properties;
    }

    public int getIndex() {
      return index;
    }

    public Class getType() {
      return type;
    }

    public EntityBean createInstance(EntityBeanIntercept parentEbi) {
      return (EntityBean) prototype._ebean_newInstance(new ExtendedIntercept(parentEbi.getOwner()._ebean_getPropertyNames().length + offset, parentEbi));
    }

    public <T> T getExtension(ExtendableBean bean) {
      EntityBean eb = (EntityBean) bean;
      return (T) bean._ebean_getExtension(index, eb._ebean_getIntercept());
    }
  }

}
