package org.tests.basic;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Ebean;
import org.junit.Assert;
import org.junit.Test;
import org.tests.model.basic.EBasicVer;
import org.tests.model.basic.UTMaster;

import javax.persistence.OptimisticLockException;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;

public class TestIUDVanilla extends BaseTestCase {

  @Test
  public void test() {

    EBasicVer e0 = new EBasicVer("vanilla");

    Ebean.save(e0);

    Assert.assertNotNull(e0.getId());
    Assert.assertNotNull(e0.getLastUpdate());

    Timestamp lastUpdate0 = e0.getLastUpdate();

    e0.setName("modified");
    Ebean.save(e0);

    Timestamp lastUpdate1 = e0.getLastUpdate();
    Assert.assertNotNull(lastUpdate1);
    Assert.assertNotSame(lastUpdate0, lastUpdate1);

    EBasicVer e2 = Ebean.getServer(null).createEntityBean(EBasicVer.class);

    e2.setId(e0.getId());
    e2.setLastUpdate(lastUpdate1);

    e2.setName("forcedUpdate");
    Ebean.update(e2);

    EBasicVer e3 = new EBasicVer("ModNoOCC");
    e3.setId(e0.getId());

    Ebean.update(e3);

    e3.setName("ModAgain");
    e3.setDescription("Banana");

    Ebean.update(e3);

  }

  @Test
  public void stateless_noOCC() {

    EBasicVer e0 = new EBasicVer("vanilla");
    Ebean.save(e0);

    EBasicVer e3 = new EBasicVer("ModNoOCC");
    e3.setId(e0.getId());
    e3.setLastUpdate(e0.getLastUpdate());

    Ebean.update(e3);

    e3.setName("ModAgain");
    //e3.setDescription("Banana");

    Ebean.update(e3);
  }

  @Test(expected = OptimisticLockException.class)
  public void modifyVersion_expect_optimisticLock() {

    UTMaster e0 = new UTMaster("save me");
    Ebean.save(e0);

    // for this case we know 42 should throw OptimisticLockException
    e0.setVersion(42);
    Ebean.update(e0);
  }

  @Test
  public void testOptimisticLockException() {
    UTMaster e0 = new UTMaster("optLock");
    DB.save(e0);

    e0 = DB.find(UTMaster.class).where().eq("name", "optLock").findOne();
    UTMaster e1 = DB.find(UTMaster.class).where().eq("name", "optLock").findOne();

    int oldVersion = e0.getVersion();
    e0.setDescription("foo");
    e0.save();
    assertThat(e0.getVersion()).isGreaterThan(oldVersion);

    e1.setDescription("bar");
    oldVersion = e1.getVersion();
    assertThatThrownBy(e1::save).isInstanceOf(OptimisticLockException.class);
    // after optimisticLockExecption, a restore of version is expected
    assertThat(e1.getVersion()).isEqualTo(oldVersion);
    // and subsequent saves must fail
    assertThatThrownBy(e1::save).isInstanceOf(OptimisticLockException.class);
  }
}
