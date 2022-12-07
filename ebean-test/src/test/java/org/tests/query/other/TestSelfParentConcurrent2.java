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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestSelfParentConcurrent2 extends BaseTestCase {
  Logger logger = LoggerFactory.getLogger(TestSelfParentConcurrent2.class);

  protected ResourceLockingHandler lockingHandler = ResourceLockingHandler.getBean();

  public static final int LOOP_COUNT = 50;

  public static String PARENT_LOCK = "parent";

  class ProcessorUpdateParent implements Runnable {
    private CountDownLatch latch;

    private Long parentId;


    private List<Long> parentIds;

    private final long startDelay;

    public ProcessorUpdateParent(CountDownLatch latch, Long parentId, long startDelay) {
      this.latch = latch;
      this.parentId = parentId;
      this.parentIds = Arrays.asList(parentId);
      this.startDelay = startDelay;
    }

    public void run() {
      try {
        Thread.sleep(startDelay);
      } catch (InterruptedException e) {
      }
      for (int i = 0; i < LOOP_COUNT; i++) {
        ResourceLock lock = lockingHandler.obtainWriteLocks("writelock thread", Arrays.asList(PARENT_LOCK));
        if (lock != null) {
          lock.release();
        }
      }
      latch.countDown();
    }
  }


    class ProcessorCreateAndDeleteChild implements Runnable {
      private CountDownLatch latch;
      private Long childId;

      private Long parentId;

      private List<Long> childIds;

      private final long startDelay;

      public ProcessorCreateAndDeleteChild(CountDownLatch latch, Long childId, Long parentId, long startDelay) {
        this.latch = latch;
        this.childId = childId;
        this.childIds = Arrays.asList(childId);
        this.parentId = parentId;
        this.startDelay = startDelay;
      }

      public void run() {
        try {
          Thread.sleep(startDelay);
        } catch (InterruptedException e) {
        }
        for (int i = 0; i < LOOP_COUNT; i++) {
          ResourceLock lock = lockingHandler.obtainReadLocks("readlock thread", Arrays.asList(PARENT_LOCK));
          if (lock != null) {
            lock.release();
          }
        }
        latch.countDown();
      }
    }

    @Test
    public void testDeadlocky() {

      CountDownLatch latch = new CountDownLatch(2);

      ExecutorService executor = Executors.newFixedThreadPool(2);

      Long parentId = 33L;
      Long childId = 44L;

      executor.submit(new ProcessorUpdateParent(latch, parentId, 0));
      executor.submit(new ProcessorCreateAndDeleteChild(latch, childId, parentId, 0));

      try {
        latch.await();  // wait until latch counted down to 0
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Completed.");

    }

}
