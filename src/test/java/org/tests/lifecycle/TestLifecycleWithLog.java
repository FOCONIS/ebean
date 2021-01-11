package org.tests.lifecycle;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Ebean;

import org.tests.model.basic.EBasicLog;
import org.tests.model.basic.EBasicWithLog;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

public class TestLifecycleWithLog extends BaseTestCase {

  private List<String> getLogs() {
    List<String> ret = DB.find(EBasicLog.class)
        .select("name")
        .findSingleAttributeList();
    DB.find(EBasicLog.class).delete();
    return ret;
  }

  @Test
  public void testCUD() {

    EBasicWithLog bean = new EBasicWithLog();
    bean.setId(1L);
    bean.setName("Test1");

    Ebean.save(bean);

    assertThat(getLogs()).contains("onPersistTrigger", "prePersist", "postPersist");

    bean.setName("Test2");

    Ebean.save(bean);

    assertThat(getLogs()).contains("onPersistTrigger", "preUpdate", "postUpdate");

    Ebean.delete(bean);

    assertThat(getLogs()).contains("onPersistTrigger", "preSoftDelete", "postSoftDelete");

    Ebean.deletePermanent(bean);

    assertThat(getLogs()).contains("onPersistTrigger", "preRemove", "postRemove");
  }

  @Test
  public void testCUDBatch() {

    EBasicWithLog bean = new EBasicWithLog();
    bean.setId(1L);
    bean.setName("Test1");

    List<EBasicWithLog> beans = Arrays.asList(bean);

    Ebean.saveAll(beans);

    assertThat(getLogs()).contains("onPersistTrigger", "prePersist", "postPersist");

    bean.setName("Test2");

    Ebean.saveAll(beans);

    assertThat(getLogs()).contains("onPersistTrigger", "preUpdate", "postUpdate");

    Ebean.deleteAll(beans);

    assertThat(getLogs()).contains("onPersistTrigger", "preSoftDelete", "postSoftDelete");

    Ebean.deleteAllPermanent(beans);

    assertThat(getLogs()).contains("onPersistTrigger", "preRemove", "postRemove");
  }

}
