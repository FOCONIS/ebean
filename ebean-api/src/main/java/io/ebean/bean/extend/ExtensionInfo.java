package io.ebean.bean.extend;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityBeanIntercept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtensionInfo implements Iterable<ExtensionAccessor> {

  public static ExtensionInfo NONE = new ExtensionInfo();
  private final int startOffset;
  private List<ExtensionAccessor> entries = new ArrayList<>();
  private final ExtensionInfo parent;
  private volatile int propertyLength = -1;
  private int[] offsets;

  /**
   * Constructor for <code>ExtensionInfo.NONE</code>.
   */
  private ExtensionInfo() {
    this.startOffset = Integer.MAX_VALUE;
    this.propertyLength = 0;
    this.parent = null;
  }

  /**
   * Called from enhancer. Each entity has a static field initialized with
   * <code>_ebean_extensions = new ExtensonInfo(thisClass._ebeanProps, superClass._ebean_extensions | null)</code>
   */
  public ExtensionInfo(String[] props, ExtensionInfo parent) {
    this.startOffset = props.length;
    this.parent = parent;
  }

  /**
   * Called from enhancer. Each class annotated with {@link EntityExtension} will show up here.
   *
   * @param prototype instance of the class that is annotated with {@link EntityExtension}
   */
  public ExtensionAccessor add(EntityBean prototype) {
    if (propertyLength != -1) {
      throw new UnsupportedOperationException("The extension is already in use and cannot be extended anymore");
    }
    Entry entry = new Entry(prototype);
    entries.add(entry);
    return entry;
  }

  /**
   * returns how many extensions are registered.
   */
  public int size() {
    init();
    return entries.size();
  }

  /**
   * Returns the additional properties, that have been added by extensions.
   */
  public int getPropertyLength() {
    init();
    return propertyLength;
  }

  private void init() {
    if (propertyLength != -1) {
      return;
    }
    synchronized (this) {
      if (propertyLength != -1) {
        return;
      }
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
        Entry entry = (Entry) entries.get(i);
        entry.index = i;
        offsets[i] = offset;
        offset += entry.getProperties().length;
        length += entry.getProperties().length;
      }
      propertyLength = length;
    }
  }

  public ExtensionAccessor get(int index) {
    init();
    return entries.get(index);
  }

  public int getOffset(ExtensionAccessor entry) {
    init();
    return offsets[entry.getIndex()];
  }

  /**
   * Required by enhancer
   */
  public int getOffset(int index) {
    init();
    return offsets[index];
  }

  public ExtensionAccessor findEntry(int propertyIndex) {
    init();
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
  public Iterator<ExtensionAccessor> iterator() {
    init();
    return entries.iterator();
  }

  public static ExtensionInfo get(Class<?> type) {
    if (ExtendableBean.class.isAssignableFrom(type)) {
      try {
        return (ExtensionInfo) type.getField("_ebean_extensions").get(null);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException("Could not read extension info from " + type, e);
      }
    }
    return null;
  }

  static class Entry implements ExtensionAccessor {
    private int index;
    private final EntityBean prototype;

    private Entry(EntityBean prototype) {
      this.prototype = prototype;
    }

    @Override
    public String[] getProperties() {
      return prototype._ebean_getPropertyNames();
    }

    @Override
    public int getIndex() {
      return index;
    }

    @Override
    public Class<?> getType() {
      return prototype.getClass();
    }

    @Override
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
