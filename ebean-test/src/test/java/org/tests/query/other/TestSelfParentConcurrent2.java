package org.tests.query.other;

import io.ebean.DB;
import io.ebean.xtest.BaseTestCase;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.model.locking.ReadWriteLock;
import org.tests.model.locking.ResourceLock;
import org.tests.model.locking.ResourceLockingHandler;
import org.tests.model.selfref.SelfParent;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNull;

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

    public Object call() throws Exception {
      try {
        Thread.sleep(startDelay);
      } catch (InterruptedException e) {
      }
      try {
        for (int i = 0; i < LOOP_COUNT; i++) {
          ResourceLock lock = lockingHandler.obtainWriteLocks(name, writeLocks);
          if (lock != null) {
            lock.release();
          }
        }
        return null;
      } finally {
        latch.countDown();
      }
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

    public Object call() throws Exception {
      try {
        Thread.sleep(startDelay);
      } catch (InterruptedException e) {
      }
      try {
        for (int i = 0; i < LOOP_COUNT; i++) {
          ResourceLock lock = lockingHandler.obtainReadLocks(name, readLocks);
          if (lock != null) {
            lock.release();
          }
        }
        return null;
      } finally {
        latch.countDown();
      }

    }


  }

  @Test
  public void testDeadlocky() throws ExecutionException {

    DB.find(ReadWriteLock.class).where().isNotNull("parent").delete();
    DB.find(ReadWriteLock.class).delete();

    int readCount = 0;
    int writeCount = 1;

    CountDownLatch latch = new CountDownLatch(readCount + writeCount);

    ExecutorService executor = Executors.newFixedThreadPool(readCount + writeCount);

    List<String> locks = Arrays.asList(PARENT_LOCK);

    List<Callable<Object>> toDos = new ArrayList<>();

    toDos.add(new ProcessorUpdateParent(latch, locks, "write", 100));
    //toDos.add(new ProcessorCreateAndDeleteChild(latch, locks, "read", 100));

    try {
      List<Future<Object>> results = executor.invokeAll(toDos);
      latch.await();
      results.get(0).get();
      //results.get(1).get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    System.out.println("Completed.");

  }

}
