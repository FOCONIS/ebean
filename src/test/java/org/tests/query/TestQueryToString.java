package org.tests.query;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import io.ebean.Filter;
import io.ebean.Query;
import org.junit.Test;
import org.tests.model.basic.CKeyParent;
import org.tests.model.basic.ResetBasicData;

import static org.assertj.core.api.Assertions.assertThat;

public class TestQueryToString extends BaseTestCase {

  @Test
  public void testToString() {

    ResetBasicData.reset();

    Query<CKeyParent> sq = Ebean.createQuery(CKeyParent.class).select("id.oneKey").alias("st0").setAutoTune(false)
        .where().eq("name", "bla").raw("st0.name = t0.name").query();

    Filter<CKeyParent> pq1 = Ebean.filter(CKeyParent.class).in("id.oneKey", sq);
    Filter<CKeyParent> pq2 = Ebean.filter(CKeyParent.class).in("id.oneKey", sq);

    assertThat(pq1.toString()).isEqualTo(pq2.toString());

    Query<CKeyParent> q1 = Ebean.find(CKeyParent.class);
    pq1.applyTo(q1.where());
    q1.findList();
    assertThat(pq1.toString()).isEqualTo(pq2.toString());
  }

}
