package org.tests.transaction;

import io.ebean.DB;
import io.ebean.Transaction;
import io.ebean.TxScope;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.model.basic.EBasic;

import static org.assertj.core.api.Assertions.assertThat;

public class TestNestedTransaction extends BaseTestCase {

  private static final Logger log = LoggerFactory.getLogger(TestNestedTransaction.class);

  private EBasic bean;

  @BeforeEach
  public void init() {
    bean = new EBasic("new");
    DB.save(bean);
  }

  private void assertClean() {
    EBasic myBean = DB.find(EBasic.class, bean.getId());
    assertThat(myBean.getName()).isEqualTo("new");
  }

  private void assertModified() {
    EBasic myBean = DB.find(EBasic.class, bean.getId());
    assertThat(myBean.getName()).isEqualTo("modified");
  }

  private void modify() {
    bean.setName("modified");
    DB.save(bean);
  }

  // ===== level 0 =======
  @Test
  public void testNested_0() {
    try (Transaction txn0 = DB.beginTransaction()) {
      modify();
      // no commit
    }
    assertClean();
  }

  @Test
  public void testNested_1() {
    try (Transaction txn0 = DB.beginTransaction()) {
      modify();
      txn0.commit();
    }
    assertModified();
  }

  // ===== level 1 =======
  @Test
  public void testNested_00() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        modify();
        // no commit
      }
      // no commit
    }
    assertClean();
  }

  @Test
  public void testNested_01() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        modify();
        // no commit
      }
      attemptCommit(txn0);
    }
    assertClean();
  }

  private void attemptCommit(Transaction txn) {
    try {
      txn.commit();
    } catch (IllegalStateException e) {
      // expected
      log.info("Expected IllegalStateException as transaction already rolled back " + e.getMessage());
    }
  }

  @Test
  public void testNested_10() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        modify();
        txn1.commit();
      }
      // no commit
    }
    assertClean();
  }

  @Test
  public void testNested_11() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        modify();
        txn1.commit();
      }
      txn0.commit();
    }
    assertModified();
  }

  // ===== level 2 =======
  @Test
  public void testNested_000() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          // no commit
        }
        // no commit
      }
      // no commit
    }
    assertClean();
  }

  @Test
  public void testNested_001() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          // no commit
        }
        // no commit
      }
      attemptCommit(txn0);
    }
    assertClean();
  }

  @Test
  public void testNested_010() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          // no commit
        }
        txn1.commit();
      }
      // no commit
    }
    assertClean();
  }

  @Test
  public void testNested_011() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          // no commit
        }
        txn1.commit();
      }
      attemptCommit(txn0);
    }
    assertClean();
  }

  @Test
  public void testNested_100() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          txn2.commit();
        }
        // no commit
      }
      // no commit
    }
    assertClean();
  }

  @Test
  public void testNested_101() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          txn2.commit();
        }
        // no commit
      }
      attemptCommit(txn0);
    }
    assertClean();
  }

  @Test
  public void testNested_110() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          txn2.commit();
        }
        txn1.commit();
      }
      // no commit
    }
    assertClean();
  }

  @Test
  public void testNested_111() {
    try (Transaction txn0 = DB.beginTransaction()) {
      try (Transaction txn1 = DB.beginTransaction()) {
        try (Transaction txn2 = DB.beginTransaction()) {
          modify();
          txn2.commit();
        }
        txn1.commit();
      }
      txn0.commit();
    }
    assertModified();
  }

  @Test
  public void test_txn_with_not_supported() {

    try (Transaction txn1 = DB.beginTransaction()) {
      assertThat(getInScopeTransaction()).isNotNull();
      getInScopeTransaction().putUserObject("foo", "bar");

      try (Transaction txn2 = DB.beginTransaction(TxScope.notSupported())) {
        // pause txn1
        try (Transaction txn3 = DB.beginTransaction()) {
          // create a new Txn scope
          txn3.commit();
        }
        txn2.commit();
      }
      // resume txn1
      assertThat(getInScopeTransaction().getUserObject("foo")).isEqualTo("bar");
    }
  }

  // 1.63.X Kontrolllauf
  @Test
  public void test_txn_63() {

    try (Transaction txn1 = DB.beginTransaction(TxScope.requiresNew())) {
      // executeKontrolle()
      // ctx.close()

      // BatchProcessor.flush()
      try (Transaction txn2 = DB.beginTransaction()) {
        // Vorgang.save() --> NotificationCallback, Hinweise sammeln, aber noch kein createNotification

        // WF-Action in JS (Zuständige berechnen und Vorgang zuordnen)
        // ScriptStatementExecutor.execute
        for (int i = 0; i < 2; i++) {

          try (Transaction txn3 = DB.beginTransaction()) {
            Object x = Transaction.current().getUserObject("x");
            if (i == 0) {
              assertThat(x).isNull();
            } else {
              assertThat(x).isEqualTo(2);
            }
            Transaction.current().putUserObject("x", 2);
            txn3.commit();
          }
        }
        txn2.commit();
      }
    }
  }

  // 1.62.X Kontrolllauf
  @Test
  public void test_txn_62() {

    // TaskRunnable.run()
    try (Transaction txn0 = DB.beginTransaction()) {
      //AbstractKontrolllauf.runWithContext()
      // protocol.runObserved
      try (Transaction txn1 = DB.beginTransaction(TxScope.mandatory())) {
        // task.run()
        // executeKontrolle()
        // ctx.close()

        // BatchProcessor.flush()
        try (Transaction txn2 = DB.beginTransaction()) {
          // Vorgang.save() --> NotificationCallback, Hinweise sammeln, aber noch kein createNotification

          //WF-Action in JS (Zuständige berechnen und Vorgang zuordnen)
          // ScriptStatementExecutor.execute
          for (int i = 0; i < 2; i++) {

            try (Transaction txn3 = DB.beginTransaction()) {
              Object x = Transaction.current().getUserObject("x");
              if (i == 0) {
                assertThat(x).isNull();
              } else {
                assertThat(x).isEqualTo(2);
              }
              Transaction.current().putUserObject("x", 2);
              txn3.commit();
            }
          }
          txn2.commit();
        }
      }
    }
  }
}
