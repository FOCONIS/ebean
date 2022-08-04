package io.ebean.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtensionInfo<T extends ExtendableBean> implements Iterable<ExtensionInfo.Entry> {

  private final int startOffset;
  private final List<Entry> entries = new ArrayList<>();
  private final ExtensionInfo<?> parent;

  private int propertyLength;

  static <T extends ExtendableBean> ExtensionInfo<T> get(Class<T> clazz) throws ReflectiveOperationException {
    Field field = clazz.getField("_ebean_extensions");
    ExtensionInfo extensions = (ExtensionInfo) field.get(null);
    if (extensions == null) {
      extensions = new ExtensionInfo(clazz);
      field.set(null, extensions);
    }
    return extensions;
  }

  private ExtensionInfo(Class<T> clazz) throws ReflectiveOperationException {
    Field targetField = clazz.getField("_ebean_props");
    String[] props = (String[]) targetField.get(null);
    this.startOffset = props.length;
    Class<? super T> superClazz = clazz.getSuperclass();
    this.parent = ExtendableBean.class.isAssignableFrom(superClazz) ? get((Class) superClazz) : null;
  }

  public Entry add(String[] props, Class type) throws ReflectiveOperationException {
    Entry entry = new Entry(propertyLength, props, entries.size(), type);
    propertyLength += props.length;
    entries.add(entry);
    return entry;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public int getPropertyLength() {
    if (parent == null) {
      return propertyLength;
    } else {
      return propertyLength + parent.getPropertyLength();
    }
  }

  public int size() {
    return parent == null ? entries.size() : entries.size() + parent.size();
  }

  public Entry get(int index) {
    if (index < entries.size()) {
      return entries.get(index);
    } else {
      if (parent == null) {
        throw new ArrayIndexOutOfBoundsException(index);
      } else {
        if (entries.isEmpty()) {
          return parent.get(index - entries.size());
        } else {
          Entry ret = parent.get(index - entries.size());
          int offset = ret.getOffset();
          for (Entry entry : entries) {
            offset += entry.getLength();
          }
          return new Entry(offset, ret.properties, ret.getIndex() + entries.size(), ret.getType(), ret.prototype);
        }
      }
    }
  }

  static class EntryIterator implements Iterator<Entry> {
    private final Iterator<Entry> current;
    private final Iterator<Entry> parent;

    public EntryIterator(Iterator<Entry> first, Iterator<Entry> next) {
      this.current = first;
      this.parent = next;
    }

    @Override
    public boolean hasNext() {
      return current.hasNext() || parent.hasNext();
    }

    @Override
    public Entry next() {
      if (current.hasNext()) {
        return current.next();
      } else {
        return parent.next();
      }
    }
  }

  @Override
  public Iterator<Entry> iterator() {
    if (parent == null) {
      return entries.iterator();
    } else {
      return new EntryIterator(entries.iterator(), parent.iterator());
    }
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
