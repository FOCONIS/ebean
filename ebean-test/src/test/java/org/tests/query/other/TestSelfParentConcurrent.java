package org.tests.query.other;

import io.ebean.DB;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.model.selfref.SelfParent;
import org.tests.transaction.TestNestedBeginRequired;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSelfParentConcurrent extends BaseTestCase {
  Logger logger = LoggerFactory.getLogger(TestSelfParentConcurrent.class);

  public static final int COUNT = 50;

  class ProcessorUpdateParent implements Runnable {
    private CountDownLatch latch;

    private Long parentId;


    private List<Long> parentIds;

    public ProcessorUpdateParent(CountDownLatch latch, Long parentId) {
      this.latch = latch;
      this.parentId = parentId;
      this.parentIds = Arrays.asList(parentId);
      SelfParent instance = new SelfParent();
      instance.setId(parentId);
      DB.save(instance);
    }

    public void run() {
      System.out.println("Started.");
      for (int i = 0; i < COUNT; i++) {
        String name = new Object().toString();
        try {
          int picked = DB.sqlUpdate("update self_parent "
              + "set name = :name "
              + "where id = :parentId and name is null and "
              + "not exists (select sq.id from (select id from self_parent where parent_id = :parentId and name is not null) sq )")
            .setParameter("name", name)
            .setParameter("parentId", parentId)
            .executeNow();
          if (picked > 0) {
            logger.debug("obtain writelock success{}", parentId);
          } else {
            logger.debug("obtain writelock failed{}", parentId);
          }
        } catch (PersistenceException e) {
          // NOP - ging halt nicht
          logger.error("could not obtain writelock {}", parentId, e);
        }
          //// release lock
        try {
          int count = DB.sqlUpdate(
              "update self_parent set name = null where id in (:ids) and name = :name")
            .setParameter("ids", parentIds)
            .setParameter("name", name)
            .executeNow();
          if (count > 0) {
            logger.debug("release writelock success{}", parentId);
          } else {
            logger.debug("release writelock failed{}", parentId);
          }


          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            //throw new RuntimeException(e);
          }
        } catch (PersistenceException e) {
          // NOP - ging halt nicht
          logger.error("could not release writelock {}", parentId, e);
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

    public ProcessorCreateAndDeleteChild(CountDownLatch latch, Long childId, Long parentId) {
      this.latch = latch;
      this.childId = childId;
      this.childIds = Arrays.asList(childId);
      this.parentId = parentId;
    }

    public void run() {
      System.out.println("Started.");
      for (int i = 0; i < COUNT; i++) {
        try {
          SelfParent instance = new SelfParent();
          instance.setName("mechild");
          instance.setId(childId);
          instance.setParent(DB.find(SelfParent.class, parentId));
          DB.save(instance);
          logger.error("child insert {}", childId);
        } catch (PersistenceException e) {
          // NOP - ging halt nicht
          logger.error("child insert failed {}", childId, e);
        }
        try {
          Thread.sleep(5);
        } catch (InterruptedException e) {
          //throw new RuntimeException(e);
        }
        try {
          int count = DB.find(SelfParent.class).where().in("id", childIds).delete();
          if (count > 0) {
            logger.debug("delete read success{}", childId);
          } else {
            logger.debug("delete read failed{}", childId);
          }
          try {
            Thread.sleep(5);
          } catch (InterruptedException e) {
            //throw new RuntimeException(e);
          }
        } catch (PersistenceException e) {
          // NOP - ging halt nicht
          logger.error("could not delete readlock {}", parentId, e);
        }
      }

      latch.countDown();
    }
  }

  @Test
  public void testDeadlocky() {

    CountDownLatch latch = new CountDownLatch(2);

    ExecutorService executor = Executors.newFixedThreadPool(2);

    Long parentId = 1L;
    Long childId = 2L;

    executor.submit(new ProcessorUpdateParent(latch, parentId));
    executor.submit(new ProcessorCreateAndDeleteChild(latch, childId, parentId));

    try {
      latch.await();  // wait until latch counted down to 0
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Completed.");

    if (DB.find(SelfParent.class).findCount() > 0) {
      // only run once
      return;
    }

  }

  public static void printNode(SelfParent o) {
    //System.out.println(o.getName());
    for (SelfParent c : o.getChildren()) {
      printNode(c);
    }
  }

}
