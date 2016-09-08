package com.avaje.tests.softdelete;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.event.BeanPersistController;
import com.avaje.ebean.event.BeanPersistRequest;
import com.avaje.tests.model.softdelete.BaseSoftDelete;

public class CountingBeanPersistController implements BeanPersistController {

	static volatile List<Class<?>> preInserts = new ArrayList<Class<?>>();
	static volatile List<Class<?>> preUpdates = new ArrayList<Class<?>>();
	static volatile List<Class<?>> preDeletes = new ArrayList<Class<?>>();
	static volatile List<Class<?>> postInserts = new ArrayList<Class<?>>();
	static volatile List<Class<?>> postUpdates = new ArrayList<Class<?>>();
	static volatile List<Class<?>> postDeletes = new ArrayList<Class<?>>();
	
	static void reset(){
		preInserts.clear();
		preUpdates.clear();
		preDeletes.clear();
		postInserts.clear();
		postUpdates.clear();
		postDeletes.clear();
	}
	
	static void printCounts(){
		printLists(true);
	}
	
	static void printLists(boolean countsOnly){
		StringBuilder sb = new StringBuilder();
		printList("preInserts", preInserts, countsOnly, sb);
		printList("postInserts", postInserts, countsOnly, sb);;
		printList("preUpdates", preUpdates, countsOnly, sb);
		printList("postUpdates", postUpdates, countsOnly, sb);
		printList("preDeletes", preDeletes, countsOnly, sb);
		printList("postDeletes", postDeletes, countsOnly, sb);
		System.out.print(sb.toString());
	}
	
	private static void printList(String title, List<Class<?>> which, boolean countsOnly, StringBuilder sb) {
		int sz = which.size();
		sb.append(title).append(": ");
		if (title.charAt(1) == 'r')
			sb.append(' ');
		sb.append(sz);
		if(!countsOnly && sz > 0){
			sb.append(" [");
			for (int i = 0; i < sz; i++){
				if (i != 0)
					sb.append(" | ");
				sb.append(which.get(i).getSimpleName());
			}
			sb.append(']');
		}
		sb.append('\n');
	}
	
	static void assertCounts(){
		assertThat(preInserts.size()).isEqualTo(postInserts.size());
		assertThat(preUpdates.size()).isEqualTo(postUpdates.size());
		assertThat(preDeletes.size()).isEqualTo(postDeletes.size());
		
	}

	@Override
	public int getExecutionOrder() {
		return 0;
	}

	@Override
	public boolean isRegisterFor(Class<?> cls) {
		return BaseSoftDelete.class.isAssignableFrom(cls);
	}

	@Override
	public boolean preInsert(BeanPersistRequest<?> request) {
		addToList(preInserts, request);
		return true;
	}

	@Override
	public boolean preUpdate(BeanPersistRequest<?> request) {
		addToList(preUpdates, request);
		return true;
	}

	@Override
	public boolean preDelete(BeanPersistRequest<?> request) {
		addToList(preDeletes, request);
		return true;
	}

	@Override
	public void postInsert(BeanPersistRequest<?> request) {
		addToList(postInserts, request);
	}

	@Override
	public void postUpdate(BeanPersistRequest<?> request) {
		addToList(postUpdates, request);
	}

	@Override
	public void postDelete(BeanPersistRequest<?> request) {
		addToList(postDeletes, request);
	}

	private void addToList(List<Class<?>> which, BeanPersistRequest<?> request){
		which.add(request.getBean().getClass());
	}
	
}