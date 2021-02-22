package org.tests.query.sqlquery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;

import javax.persistence.PersistenceException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tests.model.basic.EBasic;
import org.tests.model.basic.OrderDetail;
import org.tests.model.basic.EBasic.Status;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.DtoQuery;
import io.ebean.Query;
import io.ebean.QueryIterator;
import io.ebean.SqlQuery;
import io.ebean.annotation.ForPlatform;
import io.ebean.annotation.Platform;

public class SqlQueryCancelTest extends BaseTestCase {

  @BeforeClass
  public static void setupTestData() {
    for (int i = 0; i < 1000; i++) {
      EBasic model = new EBasic("Basic " + i);
      DB.save(model);
    }
  }

  @Test
  public void cancelSqlQueryAtBegin() {

    String sql = "select * from e_basic";
    SqlQuery query = DB.createSqlQuery(sql);
    query.cancel();
    assertThatThrownBy(query::findList)
        .isInstanceOf(PersistenceException.class)
        .hasMessageContaining("Query was cancelled");
  }

  @ForPlatform(Platform.H2)
  @Test
  public void cancelSqlDuringRun() throws SQLException {

    String sql = "select * from e_basic";
    SqlQuery query = DB.createSqlQuery(sql);
    executeDelayed(query::cancel);
    assertThatThrownBy(query::findList)
      .isInstanceOf(PersistenceException.class)
      .hasCauseInstanceOf(org.h2.jdbc.JdbcSQLTimeoutException.class);
  }

  @Test
  public void cancelOrmQueryAtBegin() {

    Query<OrderDetail> query = DB.find(OrderDetail.class);
    query.cancel();
    assertThatThrownBy(query::findList)
        .isInstanceOf(PersistenceException.class)
        .hasMessageContaining("Query was cancelled");
  }

  @ForPlatform(Platform.H2)
  @Test
  public void cancelOrmDuringRun() throws SQLException {

    Query<EBasic> query = DB.find(EBasic.class);
    executeDelayed(query::cancel);
    assertThatThrownBy(query::findList)
      .isInstanceOf(PersistenceException.class)
      .hasCauseInstanceOf(org.h2.jdbc.JdbcSQLTimeoutException.class);
  }

  @Test
  public void cancelOrmDuringIterate() throws SQLException {

    Query<EBasic> query = DB.find(EBasic.class);

    QueryIterator<EBasic> iter = query.findIterate();
    assertThat(iter.hasNext()).isTrue();
    query.cancel();
    assertThat(iter.next()).isNotNull();

    assertThatThrownBy(iter::hasNext)
      .isInstanceOf(PersistenceException.class)
      .hasMessageContaining("Query was cancelled");
  }

  public static class BasicDto {
    private Integer id;

    private Status status;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public Status getStatus() {
      return status;
    }

    public void setStatus(Status status) {
      this.status = status;
    }
  }

  @Test
  public void cancelOrmDtoQueryAtBegin() {

    DtoQuery<BasicDto> query = DB.find(EBasic.class).select("id,status").asDto(BasicDto.class);
    query.cancel();
    assertThatThrownBy(query::findList)
        .isInstanceOf(PersistenceException.class)
        .hasMessageContaining("Query was cancelled");
  }

  @ForPlatform(Platform.H2)
  @Test
  public void cancelOrmDtoDuringRun() throws SQLException {

    DtoQuery<BasicDto> query = DB.find(EBasic.class).select("id,status").asDto(BasicDto.class);
    executeDelayed(query::cancel);
    assertThatThrownBy(query::findList)
      .isInstanceOf(PersistenceException.class)
      .hasCauseInstanceOf(org.h2.jdbc.JdbcSQLTimeoutException.class);
  }

  @Test
  public void cancelSqlDtoQueryAtBegin() {

    DtoQuery<BasicDto> query = DB.findDto(BasicDto.class, "select id, status from e_basic");
    query.cancel();
    assertThatThrownBy(query::findList)
        .isInstanceOf(PersistenceException.class)
        .hasMessageContaining("Query was cancelled");
  }

  @ForPlatform(Platform.H2)
  @Test
  public void cancelSqlDtoDuringRun() throws SQLException {

    DtoQuery<BasicDto> query = DB.findDto(BasicDto.class, "select id, status from e_basic");
    executeDelayed(query::cancel);
    assertThatThrownBy(query::findList)
      .isInstanceOf(PersistenceException.class)
      .hasCauseInstanceOf(org.h2.jdbc.JdbcSQLTimeoutException.class);
  }

  private void executeDelayed(Runnable r) throws SQLException {
    SlowDownEBasic.setSelectWaitMillis(1000);
    new Thread(() -> {
      try {
        Thread.sleep(200);
        r.run();
        SlowDownEBasic.setSelectWaitMillis(0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();

  }

//  @Test
//  public void findSingleAttributeList_decimal() {
//
//    ResetBasicData.reset();
//
//    String sql = "select * from o_order_detail";
//    SqlQuery query = Ebean.createSqlQuery(sql);
//Thread canceller = new Thread() {
//  public void run() {
//    Thread.sleep(200);
//    query.cancel();
//  };
//}
//
//    List<BigDecimal> lineAmounts =
//      .findSingleAttributeList(BigDecimal.class);
//
//    assertThat(lineAmounts).isNotEmpty();
//  }

}
