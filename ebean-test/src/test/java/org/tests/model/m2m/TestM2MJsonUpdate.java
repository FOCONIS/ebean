package org.tests.model.m2m;

import io.ebean.DB;
import io.ebean.FetchPath;
import io.ebean.Transaction;
import io.ebean.test.LoggedSql;
import io.ebean.text.PathProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestM2MJsonUpdate {

  Map<Integer, String> jsons = new LinkedHashMap<>();

  @BeforeEach
  void init() {
    DB.find(MnyEdge.class).delete();
    DB.find(MnyNode.class).delete();
    MnyNode node1 = new MnyNode();
    MnyNode node2 = new MnyNode();
    node1.setId(4711);
    node2.setId(4712);
    DB.save(node1);
    DB.save(node2);
    jsons.put(4711, "{\"id\":4711,\"outgoingEdges\":[{\"id\":47114712,\"to\":{\"id\":4712,\"name\":null},\"attribute\":\"foo\"}],\"incomingEdges\":[]}");
    jsons.put(4712, "{\"id\":4712,\"outgoingEdges\":[],\"incomingEdges\":[{\"id\":47114712,\"from\":{\"id\":4711,\"name\":null},\"attribute\":\"foo\"}]}");
  }

  @Test
  public void testJsonImportSingle() {

    LoggedSql.start();
    for (Map.Entry<Integer, String> entry : jsons.entrySet()) {
      MnyNode node = DB.find(MnyNode.class)
        .fetch("outgoingEdges")
        .fetch("incomingEdges")
        .where().idEq(entry.getKey()).findOne();
      DB.json().toBean(node, entry.getValue());
      DB.save(node);
    }
    List<String> sql = LoggedSql.stop();
    sql.forEach(System.out::println);
    MnyEdge edge = DB.find(MnyEdge.class).findOne(); // there must be exactly one edge

    assertThat(edge.getFrom().getId()).isEqualTo(4711);
    assertThat(edge.getTo().getId()).isEqualTo(4712);

  }


  @Test
  public void testJsonImportWithMap() {

    LoggedSql.start();
    Map<Object, MnyNode> nodes = DB.find(MnyNode.class)
      .fetch("outgoingEdges")
      .fetch("incomingEdges")
      .where().idIn(jsons.keySet()).findMap();

    for (Map.Entry<Integer, String> entry : jsons.entrySet()) {
      MnyNode node = nodes.get(entry.getKey());
      DB.json().toBean(node, entry.getValue());
      DB.save(node);
    }
    List<String> sql = LoggedSql.stop();
    sql.forEach(System.out::println);
    MnyEdge edge = DB.find(MnyEdge.class).findOne(); // there must be exactly one edge

    assertThat(edge.getFrom().getId()).isEqualTo(4711);
    assertThat(edge.getTo().getId()).isEqualTo(4712);

  }

  @Test
  public void testJsonImportWithMapAndTxn() {

    LoggedSql.start();
    try (Transaction txn = DB.beginTransaction()) {
      Map<Object, MnyNode> nodes = DB.find(MnyNode.class)
        .fetch("outgoingEdges")
        .fetch("incomingEdges")
        .where().idIn(jsons.keySet()).findMap();

      for (Map.Entry<Integer, String> entry : jsons.entrySet()) {
        MnyNode node = nodes.get(entry.getKey());
        DB.json().toBean(node, entry.getValue());
        DB.save(node);
      }
      txn.commit();
    }
    List<String> sql = LoggedSql.stop();
    sql.forEach(System.out::println);
    MnyEdge edge = DB.find(MnyEdge.class).findOne(); // there must be exactly one edge

    assertThat(edge.getFrom().getId()).isEqualTo(4711);
    assertThat(edge.getTo().getId()).isEqualTo(4712);

  }

  @Test
  public void generateJson() {


    MnyEdge edge = new MnyEdge(DB.reference(MnyNode.class, 4711), DB.reference(MnyNode.class, 4712));
    edge.setAttribute("foo");
    DB.save(edge);

    List<MnyNode> list = DB.find(MnyNode.class)
      .fetch("outgoingEdges")
      .fetch("incomingEdges").findList();
    FetchPath path = PathProperties.parse("id,outgoingEdges(id,to,attribute),incomingEdges(id,from,attribute)");
    for (MnyNode mnyNode : list) {
      System.out.println(DB.json().toJson(mnyNode, path));

    }


  }

  @Test
  void testBasic() {
    LoggedSql.start();

    try (Transaction txn = DB.beginTransaction()) {
      MnyNode node1 = DB.find(MnyNode.class).fetch("outgoingEdges").where().idEq(4711).findOne();
      MnyNode node2 = DB.find(MnyNode.class).fetch("incomingEdges").where().idEq(4712).findOne();

      MnyEdge edge = new MnyEdge(node1, DB.reference(MnyNode.class, 4712));
      //edge = DB.getDefault().pluginApi().deduplicate(edge);
      edge.setAttribute("foo");
      node1.setOutgoingEdges(List.of(edge));
      DB.save(node1);

      edge = new MnyEdge(DB.reference(MnyNode.class, 4711), node2);
      edge.setAttribute("bar");
      node2.getIncomingEdges().add(edge);
      //DB.save(node2);
      txn.commit();
    }
    List<String> sql = LoggedSql.stop();
    sql.forEach(System.out::println);
  }
}
