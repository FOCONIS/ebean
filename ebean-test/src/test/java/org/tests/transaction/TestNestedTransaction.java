package org.tests.transaction;

import io.ebean.*;
import io.ebean.event.BeanPersistAdapter;
import io.ebean.event.BeanPersistRequest;
import io.ebean.xtest.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tests.model.basic.EBasic;
import org.tests.model.basic.EBasicVer;
import org.tests.model.json.EBasicJsonList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
  public void test_txn_with_BatchMode() {

    try (Transaction txn1 = DB.beginTransaction(TxScope.requiresNew())) {

      try (Transaction txn2 = DB.beginTransaction()) {
        txn2.setBatchMode(true);
        txn2.commit();
      }
      // resume txn1
      assertThat(txn1.isBatchMode()).isFalse();
    }
  }

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

  @Test
  public void test_txn_putUserObjectInRoot() {

    try (Transaction txn1 = DB.beginTransaction(TxScope.requiresNew())) {
      try (Transaction txn2 = DB.beginTransaction()) {
        for (int i = 0; i < 2; i++) {

          try (Transaction txn3 = DB.beginTransaction()) {
            Object x = Transaction.current().root().getUserObject("x");
            Object y = Transaction.current().getUserObject("y");
            if (i == 0) {
              assertThat(x).isNull();
              assertThat(y).isNull();
            } else {
              assertThat(x).isEqualTo(2);
              assertThat(y).isNull();
            }
            Transaction.current().root().putUserObject("x", 2);
            Transaction.current().putUserObject("y", 3);
            txn3.commit();
          }
        }
        txn2.commit();
      }
    }
  }

  @Test
  public void test_txn_nextedWithInnerBatch() {
    try (Transaction txn1 = DB.beginTransaction(TxScope.requiresNew())) {
      assertThat(EBasicVerPersistController.txnIdentifiersInsert).isEmpty();
      assertThat(EBasicVerPersistController.txnIdentifiersUpdate).isEmpty();
      for (int i = 0; i < 3; i++) {
        try (Transaction txn2 = DB.beginTransaction()) {
          txn2.putUserObject("ebasicVerTransactionId", i);
          txn2.setBatchMode(true);

          EBasicVer fromDb = DB.find(EBasicVer.class, 999 + i);
          if (fromDb != null) {
            fromDb.setDescription("Updated description");
            DB.save(fromDb);
          }

          txn2.flush();

          EBasicVer basic = new EBasicVer("New name");
          basic.setId(1000 + i);
          basic.setDescription("New description");
          DB.save(basic);

          txn2.flush();

          basic.setOther("Other" + i);
          DB.save(basic);

//          txn2.flush();
          txn2.commit();
        }

        if (i == 0) {
          assertThat(EBasicVerPersistController.txnIdentifiersInsert).containsExactly(0);
          assertThat(EBasicVerPersistController.txnIdentifiersUpdate).containsExactly(0);
        } else if (i == 1) {
          assertThat(EBasicVerPersistController.txnIdentifiersInsert).containsExactly(0, 1);
          assertThat(EBasicVerPersistController.txnIdentifiersUpdate).containsExactly(0, 1, 1);
        } else if (i == 2) {
          assertThat(EBasicVerPersistController.txnIdentifiersInsert).containsExactly(0, 1, 2);
          assertThat(EBasicVerPersistController.txnIdentifiersUpdate).containsExactly(0, 1, 1, 2, 2);
        } else {
          throw new IllegalStateException("Unexpected index");
        }
      }
    }
  }

  public static class EBasicVerPersistController extends BeanPersistAdapter {

    static List<Integer> txnIdentifiersInsert = new ArrayList<>();
    static List<Integer> txnIdentifiersUpdate = new ArrayList<>();

    @Override
    public boolean isRegisterFor(Class<?> cls) {
      return EBasicVer.class.isAssignableFrom(cls);
    }

    @Override
    public boolean preInsert(BeanPersistRequest<?> request) {
      txnIdentifiersInsert.add((Integer) request.transaction().getUserObject("ebasicVerTransactionId"));
      return true;
    }

    @Override
    public boolean preUpdate(BeanPersistRequest<?> request) {
      txnIdentifiersUpdate.add((Integer) request.transaction().getUserObject("ebasicVerTransactionId"));
      return true;
    }
  }

}
