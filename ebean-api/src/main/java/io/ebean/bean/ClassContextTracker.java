package io.ebean.bean;

import java.util.Iterator;
import java.util.ServiceLoader;

public interface ClassContextTracker {

  int getThreshold(Class<?> rootType);

  int log(Class<?> rootType, int size, int threshold);

  ClassContextTracker INSTANCE = createInstance();

  static ClassContextTracker createInstance() {

    Iterator<ClassContextTracker> loader = ServiceLoader.load(ClassContextTracker.class).iterator();
    if (loader.hasNext()) {
      return loader.next();
    }
    return new ClassContextTracker() {
      @Override
      public int getThreshold(Class<?> rootType) {
        return -1;
      }

      @Override
      public int log(Class<?> rootType, int size, int threshold) {
        return -1;
      }
    };
  }

}
