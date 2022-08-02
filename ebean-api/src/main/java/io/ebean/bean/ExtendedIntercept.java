package io.ebean.bean;

import io.ebean.ValuePair;

import java.util.Map;
import java.util.Set;

/**
 * @author Roland Praml, FOCONIS AG
 */
public class ExtendedIntercept implements EntityBeanIntercept {
  private final EntityBeanIntercept parent;
  private final int offset;

  public ExtendedIntercept(int offset, EntityBeanIntercept parent) {
    this.parent = parent;
    this.offset = offset;
  }

  @Override
  public EntityBean getOwner() {
    return parent.getOwner();
  }

  @Override
  public PersistenceContext getPersistenceContext() {
    return parent.getPersistenceContext();
  }

  @Override
  public void setPersistenceContext(PersistenceContext persistenceContext) {
    parent.setPersistenceContext(persistenceContext);
  }

  @Override
  public void setNodeUsageCollector(NodeUsageCollector usageCollector) {
    parent.setNodeUsageCollector(usageCollector);
  }

  @Override
  public Object getOwnerId() {
    return parent.getOwnerId();
  }

  @Override
  public void setOwnerId(Object ownerId) {
    parent.setOwnerId(ownerId);
  }

  @Override
  public Object getEmbeddedOwner() {
    return parent.getEmbeddedOwner();
  }

  @Override
  public int getEmbeddedOwnerIndex() {
    return parent.getEmbeddedOwnerIndex();
  }

  @Override
  public void clearGetterCallback() {
    parent.clearGetterCallback();
  }

  @Override
  public void registerGetterCallback(PreGetterCallback getterCallback) {
    parent.registerGetterCallback(getterCallback);
  }

  @Override
  public void setEmbeddedOwner(EntityBean parentBean, int embeddedOwnerIndex) {
    parent.setEmbeddedOwner(parentBean, embeddedOwnerIndex);
  }

  @Override
  public void setBeanLoader(BeanLoader beanLoader, PersistenceContext ctx) {
    parent.setBeanLoader(beanLoader, ctx);
  }

  @Override
  public void setBeanLoader(BeanLoader beanLoader) {
    parent.setBeanLoader(beanLoader);
  }

  @Override
  public boolean isFullyLoadedBean() {
    return parent.isFullyLoadedBean();
  }

  @Override
  public void setFullyLoadedBean(boolean fullyLoadedBean) {
    parent.setFullyLoadedBean(fullyLoadedBean);
  }

  @Override
  public boolean isPartial() {
    return parent.isPartial();
  }

  @Override
  public boolean isDirty() {
    return parent.isDirty();
  }

  @Override
  public void setEmbeddedDirty(int embeddedProperty) {
    parent.setEmbeddedDirty(embeddedProperty + offset);
  }

  @Override
  public void setDirty(boolean dirty) {
    parent.setDirty(dirty);
  }

  @Override
  public boolean isNew() {
    return parent.isNew();
  }

  @Override
  public boolean isNewOrDirty() {
    return parent.isNewOrDirty();
  }

  @Override
  public boolean hasIdOnly(int idIndex) {
    return parent.hasIdOnly(idIndex + offset);
  }

  @Override
  public boolean isReference() {
    return parent.isReference();
  }

  @Override
  public void setReference(int idPos) {
    parent.setReference(idPos + offset);
  }

  @Override
  public void setLoadedFromCache(boolean loadedFromCache) {
    parent.setLoadedFromCache(loadedFromCache);
  }

  @Override
  public boolean isLoadedFromCache() {
    return parent.isLoadedFromCache();
  }

  @Override
  public boolean isReadOnly() {
    return parent.isReadOnly();
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    parent.setReadOnly(readOnly);
  }

  @Override
  public void setForceUpdate(boolean forceUpdate) {
    parent.setForceUpdate(forceUpdate);
  }

  @Override
  public boolean isUpdate() {
    return parent.isUpdate();
  }

  @Override
  public boolean isLoaded() {
    return parent.isLoaded();
  }

  @Override
  public void setNew() {
    parent.setNew();
  }

  @Override
  public void setLoaded() {
    parent.setLoaded();
  }

  @Override
  public void setLoadedLazy() {
    parent.setLoadedLazy();
  }

  @Override
  public void setLazyLoadFailure(Object ownerId) {
    parent.setLazyLoadFailure(ownerId);
  }

  @Override
  public boolean isLazyLoadFailure() {
    return parent.isLazyLoadFailure();
  }

  @Override
  public boolean isDisableLazyLoad() {
    return parent.isDisableLazyLoad();
  }

  @Override
  public void setDisableLazyLoad(boolean disableLazyLoad) {
    parent.setDisableLazyLoad(disableLazyLoad);
  }

  @Override
  public void setEmbeddedLoaded(Object embeddedBean) {
    parent.setEmbeddedLoaded(embeddedBean);
  }

  @Override
  public boolean isEmbeddedNewOrDirty(Object embeddedBean) {
    return parent.isEmbeddedNewOrDirty(embeddedBean);
  }

  @Override
  public Object getOrigValue(int propertyIndex) {
    return parent.getOrigValue(propertyIndex + offset);
  }

  @Override
  public int findProperty(String propertyName) {
    return parent.findProperty(propertyName);
  }

  @Override
  public String getProperty(int propertyIndex) {
    return parent.getProperty(propertyIndex + offset);
  }

  @Override
  public int getPropertyLength() {
    return parent.getPropertyLength();
  }

  @Override
  public void setPropertyLoaded(String propertyName, boolean loaded) {
    parent.setPropertyLoaded(propertyName, loaded);
  }

  @Override
  public void setPropertyUnloaded(int propertyIndex) {
    parent.setPropertyUnloaded(propertyIndex + offset);
  }

  @Override
  public void setLoadedProperty(int propertyIndex) {
    parent.setLoadedProperty(propertyIndex + offset);
  }

  @Override
  public void setLoadedPropertyAll() {
    parent.setLoadedPropertyAll();
  }

  @Override
  public boolean isLoadedProperty(int propertyIndex) {
    return parent.isLoadedProperty(propertyIndex + offset);
  }

  @Override
  public boolean isChangedProperty(int propertyIndex) {
    return parent.isChangedProperty(propertyIndex + offset);
  }

  @Override
  public boolean isDirtyProperty(int propertyIndex) {
    return parent.isDirtyProperty(propertyIndex + offset);
  }

  @Override
  public void markPropertyAsChanged(int propertyIndex) {
    parent.markPropertyAsChanged(propertyIndex + offset);
  }

  @Override
  public void setChangedProperty(int propertyIndex) {
    parent.setChangedProperty(propertyIndex + offset);
  }

  @Override
  public void setChangeLoaded(int propertyIndex) {
    parent.setChangeLoaded(propertyIndex + offset);
  }

  @Override
  public void setEmbeddedPropertyDirty(int propertyIndex) {
    parent.setEmbeddedPropertyDirty(propertyIndex + offset);
  }

  @Override
  public void setOriginalValue(int propertyIndex, Object value) {
    parent.setOriginalValue(propertyIndex + offset, value);
  }

  @Override
  public void setOriginalValueForce(int propertyIndex, Object value) {
    parent.setOriginalValueForce(propertyIndex + offset, value);
  }

  @Override
  public void setNewBeanForUpdate() {
    parent.setNewBeanForUpdate();
  }

  @Override
  public Set<String> getLoadedPropertyNames() {
    return parent.getLoadedPropertyNames();
  }

  @Override
  public boolean[] getDirtyProperties() {
    return parent.getDirtyProperties();
  }

  @Override
  public Set<String> getDirtyPropertyNames() {
    return parent.getDirtyPropertyNames();
  }

  @Override
  public void addDirtyPropertyNames(Set<String> props, String prefix) {
    parent.addDirtyPropertyNames(props, prefix);
  }

  @Override
  public boolean hasDirtyProperty(Set<String> propertyNames) {
    return parent.hasDirtyProperty(propertyNames);
  }

  @Override
  public Map<String, ValuePair> getDirtyValues() {
    return parent.getDirtyValues();
  }

  @Override
  public void addDirtyPropertyValues(Map<String, ValuePair> dirtyValues, String prefix) {
    parent.addDirtyPropertyValues(dirtyValues, prefix);
  }

  @Override
  public void addDirtyPropertyValues(BeanDiffVisitor visitor) {
    parent.addDirtyPropertyValues(visitor);
  }

  @Override
  public StringBuilder getDirtyPropertyKey() {
    return parent.getDirtyPropertyKey();
  }

  @Override
  public void addDirtyPropertyKey(StringBuilder sb) {
    parent.addDirtyPropertyKey(sb);
  }

  @Override
  public StringBuilder getLoadedPropertyKey() {
    return parent.getLoadedPropertyKey();
  }

  @Override
  public boolean[] getLoaded() {
    return parent.getLoaded();
  }

  @Override
  public int getLazyLoadPropertyIndex() {
    return parent.getLazyLoadPropertyIndex() - offset;
  }

  @Override
  public String getLazyLoadProperty() {
    return parent.getLazyLoadProperty();
  }

  @Override
  public void loadBean(int loadProperty) {
    parent.loadBean(loadProperty);
  }

  @Override
  public void loadBeanInternal(int loadProperty, BeanLoader loader) {
    parent.loadBeanInternal(loadProperty + offset, loader);
  }

  @Override
  public void initialisedMany(int propertyIndex) {
    parent.initialisedMany(propertyIndex + offset);
  }

  @Override
  public void preGetterCallback(int propertyIndex) {
    parent.preGetterCallback(propertyIndex + offset);
  }

  @Override
  public void preGetId() {
    parent.preGetId();
  }

  @Override
  public void preGetter(int propertyIndex) {
    parent.preGetter(propertyIndex + offset);
  }

  @Override
  public void preSetterMany(boolean interceptField, int propertyIndex, Object oldValue, Object newValue) {
    parent.preSetterMany(interceptField, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void setChangedPropertyValue(int propertyIndex, boolean setDirtyState, Object origValue) {
    parent.setChangedPropertyValue(propertyIndex + offset, setDirtyState, origValue);
  }

  @Override
  public void setDirtyStatus() {
    parent.setDirtyStatus();
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, Object oldValue, Object newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, boolean oldValue, boolean newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, int oldValue, int newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, long oldValue, long newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, double oldValue, double newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, float oldValue, float newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, short oldValue, short newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, char oldValue, char newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, byte oldValue, byte newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, char[] oldValue, char[] newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void preSetter(boolean intercept, int propertyIndex, byte[] oldValue, byte[] newValue) {
    parent.preSetter(intercept, propertyIndex + offset, oldValue, newValue);
  }

  @Override
  public void setOldValue(int propertyIndex, Object oldValue) {
    parent.setOldValue(propertyIndex + offset, oldValue);
  }

  @Override
  public int getSortOrder() {
    return parent.getSortOrder();
  }

  @Override
  public void setSortOrder(int sortOrder) {
    parent.setSortOrder(sortOrder);
  }

  @Override
  public void setDeletedFromCollection(boolean deletedFromCollection) {
    parent.setDeletedFromCollection(deletedFromCollection);
  }

  @Override
  public boolean isOrphanDelete() {
    return parent.isOrphanDelete();
  }

  @Override
  public void setLoadError(int propertyIndex, Exception t) {
    parent.setLoadError(propertyIndex + offset, t);
  }

  @Override
  public Map<String, Exception> getLoadErrors() {
    return parent.getLoadErrors();
  }

  @Override
  public boolean isChangedProp(int propertyIndex) {
    return parent.isChangedProp(propertyIndex + offset);
  }

  @Override
  public MutableValueInfo mutableInfo(int propertyIndex) {
    return parent.mutableInfo(propertyIndex + offset);
  }

  @Override
  public void mutableInfo(int propertyIndex, MutableValueInfo info) {
    parent.mutableInfo(propertyIndex + offset, info);
  }

  @Override
  public void mutableNext(int propertyIndex, MutableValueNext next) {
    parent.mutableNext(propertyIndex + offset, next);
  }

  @Override
  public String mutableNext(int propertyIndex) {
    return parent.mutableNext(propertyIndex + offset);
  }

  @Override
  public Object getValue(int propertyIndex) {
    return parent.getValue(propertyIndex + offset);
  }

  @Override
  public Object getValueIntercept(int propertyIndex) {
    return parent.getValueIntercept(propertyIndex + offset);
  }
}
