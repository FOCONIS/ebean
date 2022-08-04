package io.ebean.bean;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtensionInfo extends AbstractList<ExtensionInfo.Entry> {

  private final int startOffset;
  private final List<Entry> entries = new ArrayList<>();

  private int propertyLength;

  public ExtensionInfo(int offset) {
    this.startOffset = offset;
  }

  public Entry add(String[] props, Class type) throws ReflectiveOperationException {
    Entry entry = new Entry(startOffset + propertyLength, props, entries.size(), type);
    propertyLength+=props.length;
    entries.add(entry);
    return entry;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public int getPropertyLength() {
    return propertyLength;
  }

  public int size() {
    return entries.size();
  }

  public Entry get(int index) {
    return entries.get(index);
  }

  public static class Entry {
    private final int start;
    private final String[] properties;

    private final int index;
    private final EntityBean prototype;
    private final Class type;

    private Entry(int start, String[] properties, int index, Class type) throws ReflectiveOperationException {
      this.start = start;
      this.properties = properties;
      this.index = index;
      this.type = type;
      this.prototype = (EntityBean) type.getConstructor().newInstance();
    }

    public int getStart() {
      return start;
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
      return (EntityBean) prototype._ebean_newInstance(new ExtendedIntercept(start, parentEbi));
    }

    public <T> T getExtension(ExtendableBean bean) {
      EntityBean eb = (EntityBean) bean;
      return (T) bean._ebean_getExtension(index, eb._ebean_getIntercept());
    }
  }

}
