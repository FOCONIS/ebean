package io.ebean.bean;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtensionInfo extends AbstractList<ExtensionInfo.Entry> {

  private final int startOffset;
  private final List<Entry> entries = new ArrayList<>();

  public ExtensionInfo(int offset) {
    this.startOffset = offset;
  }

  public int add(int start, int length, Class type) throws ReflectiveOperationException {
    entries.add(new Entry(start,length, entries.size(),type));
    return entries.size()-1;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public int size() {
    return entries.size();
  }

  public Entry get(int index) {
    return entries.get(index);
  }

  public static class Entry {
    private final int start;
    private final int length;

    private final int index;
    private final EntityBean prototype;
    private final Class type;

    private Entry(int start, int length, int index, Class type) throws ReflectiveOperationException {
      this.start = start;
      this.length = length;
      this.index = index;
      this.type = type;
      this.prototype = (EntityBean) type.getConstructor().newInstance();
    }

    public int getStart() {
      return start;
    }

    public int getLength() {
      return length;
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
  }

}
