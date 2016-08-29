package com.avaje.ebeaninternal.server.deploy;

import com.avaje.ebean.event.BeanPostConstruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles multiple BeanPostLoad's for a given entity type.
 */
public class ChainedBeanPostConstruct implements BeanPostConstruct {

	private final List<BeanPostConstruct> list;

	private final BeanPostConstruct[] chain;

	/**
	 * Construct given the list of BeanPostCreate's.
	 */
	public ChainedBeanPostConstruct(List<BeanPostConstruct> list) {
		this.list = list;
		this.chain = list.toArray(new BeanPostConstruct[list.size()]);
	}
	
	/**
	 * Register a new BeanPostCreate and return the resulting chain.
	 */
	public ChainedBeanPostConstruct register(BeanPostConstruct c) {
		if (list.contains(c)){
			return this;
		} else {
			List<BeanPostConstruct> newList = new ArrayList<BeanPostConstruct>();
			newList.addAll(list);
			newList.add(c);
			
			return new ChainedBeanPostConstruct(newList);
		}
	}
	
	/**
	 * De-register a BeanPostCreate and return the resulting chain.
	 */
	public ChainedBeanPostConstruct deregister(BeanPostConstruct c) {
		if (!list.contains(c)){
			return this;
		} else {
			ArrayList<BeanPostConstruct> newList = new ArrayList<BeanPostConstruct>();
			newList.addAll(list);
			newList.remove(c);
			
			return new ChainedBeanPostConstruct(newList);
		}
	}

  /**
   * Return the size of the chain.
   */
  protected int size() {
    return chain.length;
  }

  @Override
  public boolean isRegisterFor(Class<?> cls) {
    // never called
    return false;
  }

  /**
   * Fire postLoad on all registered BeanPostCreate implementations.
   */
  @Override
	public void postConstruct(Object bean) {
		for (int i = 0; i < chain.length; i++) {
			chain[i].postConstruct(bean);
		}
	}
}
