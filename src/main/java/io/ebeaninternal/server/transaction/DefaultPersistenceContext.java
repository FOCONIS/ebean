package io.ebeaninternal.server.transaction;

import io.ebean.bean.PersistenceContext;
import io.ebeaninternal.api.Monitor;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of PersistenceContext.
 * <p>
 * Ensures only one instance of a bean is used according to its type and unique
 * id.
 * </p>
 * <p>
 * PersistenceContext lives on a Transaction and as such is expected to only
 * have a single thread accessing it at a time. This is not expected to be used
 * concurrently.
 * </p>
 * <p>
 * Duplicate beans are ones having the same type and unique id value. These are
 * considered duplicates and replaced by the bean instance that was already
 * loaded into the PersistenceContext.
 * </p>
 */
public final class DefaultPersistenceContext implements PersistenceContext {

  /**
   * Map used hold caches. One cache per bean type.
   */
  private final HashMap<Class<?>, ClassContext> typeCache = new HashMap<>();

  private final Monitor monitor = new Monitor();

  private final ReferenceQueue<Object> queue = new ReferenceQueue<>();

  /**
   * When we are inside an iterate loop, we will add only WeakReferences. This
   * allows the JVM GC to collect beans, which are not referenced elsewhere. In
   * normal operation, we will use hard references, to avoid performance impact
   */
  private int iterateDepth;

  /**
   * Create a new PersistenceContext.
   */
  public DefaultPersistenceContext() {
  }

  @Override
  public void beginIterate() {
    synchronized (monitor) {
      iterateDepth++;
    }
  }

  @Override
  public void endIterate() {
    synchronized (monitor) {
      iterateDepth--;
      expungeStaleEntries(); // when leaving the iterator, cleanup.
    }
  }

  /**
   * Set an object into the PersistenceContext.
   */
  @Override
  public void put(Class<?> rootType, Object id, Object bean) {
    synchronized (monitor) {
      expungeStaleEntries();
      getClassContext(rootType).useReferences(iterateDepth > 0).put(id, bean);
    }
  }

  @Override
  public Object putIfAbsent(Class<?> rootType, Object id, Object bean) {
    synchronized (monitor) {
      expungeStaleEntries();
      return getClassContext(rootType).useReferences(iterateDepth > 0).putIfAbsent(id, bean);
    }
  }

  /**
   * Return an object given its type and unique id.
   */
  @Override
  public Object get(Class<?> rootType, Object id) {
    synchronized (monitor) {
      expungeStaleEntries();
      return getClassContext(rootType).get(id);
    }
  }

  @Override
  public WithOption getWithOption(Class<?> rootType, Object id) {
    synchronized (monitor) {
      expungeStaleEntries();
      return getClassContext(rootType).getWithOption(id);
    }
  }

  /**
   * Return the number of beans of the given type in the persistence context.
   */
  @Override
  public int size(Class<?> rootType) {
    synchronized (monitor) {
      expungeStaleEntries();
      ClassContext classMap = typeCache.get(rootType);
      return classMap == null ? 0 : classMap.size();
    }
  }

  /**
   * Clear the PersistenceContext.
   */
  @Override
  public void clear() {
    synchronized (monitor) {
      typeCache.clear();
      expungeStaleEntries();
    }
  }

  @Override
  public void clear(Class<?> rootType) {
    synchronized (monitor) {
      ClassContext classMap = typeCache.get(rootType);
      if (classMap != null) {
        classMap.clear();
      }
      expungeStaleEntries();
    }
  }

  @Override
  public void deleted(Class<?> rootType, Object id) {
    synchronized (monitor) {
      ClassContext classMap = typeCache.get(rootType);
      if (classMap != null && id != null) {
        classMap.deleted(id);
      }
      expungeStaleEntries();
    }
  }

  @Override
  public void clear(Class<?> rootType, Object id) {
    synchronized (monitor) {
      ClassContext classMap = typeCache.get(rootType);
      if (classMap != null && id != null) {
        classMap.remove(id);
      }
      expungeStaleEntries();
    }
  }


  /**
   * When there is a queue, poll it to remove stale entries from the map. Note:
   * This is always done AFTER <code>useReferences</code> was called with
   * <code>true</code>. Polling an empty queue has no performance impact.
   */
  private void expungeStaleEntries() {
    Reference<?> ref;
    while ((ref = queue.poll()) != null) {
      ((BeanRef) ref).expunge();
    }
  }

  @Override
  public String toString() {
    synchronized (monitor) {
      expungeStaleEntries();
      return typeCache.toString();
    }
  }

  private ClassContext getClassContext(Class<?> rootType) {

    return typeCache.computeIfAbsent(rootType, k -> new ClassContext(k, queue));
  }

  private static class ClassContext {

    private final Map<Object, Object> map = new HashMap<>();
    private final Class<?> rootType;
    private final ReferenceQueue<Object> queue;
    private Set<Object> deleteSet;
    private boolean useReferences;
    private int weakCount;

    private ClassContext(Class<?> rootType, ReferenceQueue<Object> queue) {
      this.rootType = rootType;
      this.queue = queue;
    }

    /**
     * When called with "true", initialize referenceQueue and store BeanRefs instead
     * of real object references.
     */
    private ClassContext useReferences(boolean useReferences) {
      this.useReferences = useReferences;
      return this;
    }

    @Override
    public String toString() {
      return "size:" + map.size() + " (" + weakCount + " weak)";
    }

    private Object get(Object id) {
      Object ret = map.get(id);
      if (ret instanceof BeanRef) {
        return ((BeanRef) ret).get();
      } else {
        return ret;
      }
    }

    private WithOption getWithOption(Object id) {
      if (deleteSet != null && deleteSet.contains(id)) {
        return WithOption.DELETED;
      }
      Object bean = get(id);
      return (bean == null) ? null : new WithOption(bean);
    }

    private Object putIfAbsent(Object id, Object bean) {
      Object existingValue = get(id);
      if (existingValue != null) {
        // it is not absent
        return existingValue;
      }
      // put the new value and return null indicating the put was successful
      put(id, bean);
      return null;
    }

    private void put(Object id, Object b) {
      if (useReferences) {
        weakCount++;
        map.put(id, new BeanRef(this, id, b, queue));
      } else {
        map.put(id, b);
      }
    }

    private int size() {
      return map.size();
    }

    private void clear() {
      map.clear();
      weakCount = 0;
    }

    private void remove(Object id) {
      Object ret = map.remove(id);
      if (ret instanceof BeanRef) {
        weakCount--;
      }
    }

    private void deleted(Object id) {
      if (deleteSet == null) {
        deleteSet = new HashSet<>();
      }
      deleteSet.add(id);
      remove(id);
    }
  }

  private static class BeanRef extends WeakReference<Object> {

    private final ClassContext classContext;
    private final Object key;

    private BeanRef(ClassContext classContext, Object key, Object referent, ReferenceQueue<? super Object> q) {
      super(referent, q);
      this.classContext = classContext;
      this.key = key;
    }

    private void expunge() {
      classContext.remove(key);
    }

  }

}
