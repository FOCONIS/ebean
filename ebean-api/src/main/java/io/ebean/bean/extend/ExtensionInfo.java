package io.ebean.bean.extend;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtensionInfo implements Iterable<ExtensionInfo.Entry> {

  public static ExtensionInfo NONE = new ExtensionInfo();
  private final int startOffset;
  private List<Entry> entries = new ArrayList<>();
  private final ExtensionInfo parent;
  private int propertyLength = -1;

  private int[] offsets;

  static <T extends ExtendableBean> ExtensionInfo get(Class<T> clazz) throws ReflectiveOperationException {
    Field field = clazz.getField("_ebean_extensions");
    return (ExtensionInfo) field.get(null);
  }

  private ExtensionInfo() {
    this.startOffset = Integer.MAX_VALUE;
    this.propertyLength = 0;
    this.parent = null;
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
    Entry entry = new Entry(props, type);
    entries.add(entry);
    return entry;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public int getPropertyLength() {
    if (propertyLength == -1) {
      init();
    }
    return propertyLength;
  }

  public int size() {
    return entries.size();
  }

  public synchronized void init() {
    if (propertyLength == -1) {
      int length = 0;
      if (parent != null) {
        parent.init();
        if (entries.isEmpty()) {
          entries = parent.entries;
        } else {
          entries.addAll(0, parent.entries);
        }
      }
      offsets = new int[entries.size()];
      int offset = startOffset;
      for (int i = 0; i < entries.size(); i++) {
        Entry entry = entries.get(i);
        entry.index = i;
        offsets[i] = offset;
        offset += entry.getLength();
        length += entry.getLength();
      }
      propertyLength = length;
    }
  }

  public Entry get(int index) {
    return entries.get(index);
  }

  public int getOffset(Entry entry) {
    return offsets[entry.getIndex()];
  }

  public int getOffset(int index) {
    return offsets[index];
  }

/*
  public ExtensionInfo.Entry getExtensionEntry(int index) {

    index -= startOffset;
    for (int i = 0; i < entries.size(); i++) {
      index -= entries.get(i).getLength();
      if (index < 0) {
        return entries.get(i);
      }
    }

    throw new IllegalArgumentException("Index invalid");
  }
*/

  public Entry findEntry(int propertyIndex) {
    if (propertyIndex < startOffset) {
      return null;
    }
    int pos = Arrays.binarySearch(offsets, propertyIndex);
    if (pos == -1) {
      return null;
    }
    if (pos < 0) {
      pos = -2 - pos;
    }
    return entries.get(pos);
  }

  @Override
  public Iterator<Entry> iterator() {
    return entries.iterator();
  }

  public static class Entry implements ExtensionAccessor {
    private final String[] properties;

    private int index;
    private final EntityBean prototype;
    private final Class type;

    private Entry(String[] properties, Class type) throws ReflectiveOperationException {
      this.properties = properties;
      this.type = type;
      this.prototype = (EntityBean) type.getConstructor().newInstance();
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

    public EntityBean createInstance(int offset, EntityBeanIntercept parentEbi) {
      return (EntityBean) prototype._ebean_newInstanceIntercept(new ExtendedIntercept(offset, parentEbi));
    }

    @Override
    public <T> T getExtension(ExtendableBean bean) {
      EntityBean eb = (EntityBean) bean;
      return (T) bean._ebean_getExtension(index, eb._ebean_getIntercept());
    }
  }
}
