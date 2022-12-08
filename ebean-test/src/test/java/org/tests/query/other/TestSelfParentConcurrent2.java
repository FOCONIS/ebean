package org.tests.query.other;

import io.ebean.DB;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.model.locking.ResourceLock;
import org.tests.model.locking.ResourceLockingHandler;
import org.tests.model.selfref.SelfParent;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSelfParentConcurrent2 extends BaseTestCase {
  Logger logger = LoggerFactory.getLogger(TestSelfParentConcurrent2.class);

  protected ResourceLockingHandler lockingHandler = ResourceLockingHandler.getBean();

  public static final int LOOP_COUNT = 50;

  public static String PARENT_LOCK = "parent";

  class ProcessorUpdateParent implements Callable<Object> {
    private CountDownLatch latch;

    private List<String> writeLocks;

    private String name;

    private final long startDelay;

    public ProcessorUpdateParent(CountDownLatch latch, List<String> writeLocks, String name, long startDelay) {
      this.latch = latch;
      this.writeLocks = writeLocks;
      this.name = name;
      this.startDelay = startDelay;
    }

    public Object call() {
      try {
        Thread.sleep(startDelay);
      } catch (InterruptedException e) {
      }
      for (int i = 0; i < LOOP_COUNT; i++) {
        ResourceLock lock = lockingHandler.obtainWriteLocks(name, Arrays.asList(PARENT_LOCK));
        if (lock != null) {
          lock.release();
        }
      }
      latch.countDown();
      return null;
    }
  }


  class ProcessorCreateAndDeleteChild implements Callable<Object> {
    private CountDownLatch latch;
    private List<String> readLocks;

    private String name;

    private final long startDelay;

    public ProcessorCreateAndDeleteChild(CountDownLatch latch, List<String> readLocks, String name, long startDelay) {
      this.latch = latch;
      this.readLocks = readLocks;
      this.name = name;
      this.startDelay = startDelay;
    }

    public Object call() {
      try {
        Thread.sleep(startDelay);
      } catch (InterruptedException e) {
      }
      for (int i = 0; i < LOOP_COUNT; i++) {
        ResourceLock lock = lockingHandler.obtainReadLocks(name, Arrays.asList(PARENT_LOCK));
        if (lock != null) {
          lock.release();
        }
      }
      latch.countDown();
      return null;
    }
  }

  @Test
  public void testDeadlocky() {

    int readCount = 5;
    int writeCount = 1;

    CountDownLatch latch = new CountDownLatch(readCount + writeCount);

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Long parentId = 33L;
    Long childId = 44L;

    List<String> locks = Arrays.asList(PARENT_LOCK);

    executor.submit(new ProcessorUpdateParent(latch, locks, "write", 0));
    executor.submit(new ProcessorCreateAndDeleteChild(latch, locks, "read", 0));

    try {
      latch.await();  // wait until latch counted down to 0
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Completed.");

  }

}
