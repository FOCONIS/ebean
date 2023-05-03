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

public class TestM2MJsonUpdate {

  Map<Integer, String> jsons = new LinkedHashMap<>();

  @Test
  public void doubleInsert() {

    DRol role = new DRol("rol");
    role.save();

    DCredit credit = new DCredit("x1");
    credit.getDroles().add(role);
    role.getCredits().add(credit);
    credit.save();

    DRot rot = new DRot("rot");
    rot.getCroles().add(role);
    DB.save(rot);
  }

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
  }

  @Test
  public void testJsonImport() {


    jsons.put(4711, "{\"id\":4711,\"outgoingEdges\":[{\"id\":47114712,\"to\":{\"id\":4712}}],\"incomingEdges\":[]}");
    jsons.put(4712, "{\"id\":4712,\"outgoingEdges\":[],\"incomingEdges\":[{\"id\":47114712,\"from\":{\"id\":4711}}]}");
  }
  @Test
  public void testCycle() {


    LoggedSql.start();

    try (Transaction txn = DB.beginTransaction()) {
      MnyNode node1 = DB.find(MnyNode.class).fetch("outgoingEdges").where().idEq(4711).findOne();
      MnyNode node2 = DB.find(MnyNode.class).fetch("incomingEdges").where().idEq(4712).findOne();

      MnyEdge edge = new MnyEdge(node1, DB.reference(MnyNode.class, 4712));
      edge = DB.getDefault().pluginApi().deduplicate(edge);
      edge.setDebug("D1");
      node1.getOutgoingEdges().add(edge);
      DB.save(node1);

      //node2 = DB.find(MnyNode.class, node2.getId());
      edge = new MnyEdge(DB.reference(MnyNode.class, 4711), node2);
      edge = DB.getDefault().pluginApi().deduplicate(edge);
      edge.setDebug("D2");
      node2.getIncomingEdges().add(edge);
      DB.save(node2);
      txn.commit();
    }
    List<String> sql = LoggedSql.stop();
    sql.forEach(System.out::println);

    List<MnyNode> list = DB.find(MnyNode.class).select("id")
      .fetch("outgoingEdges")
      .fetch("incomingEdges").findList();
      FetchPath path = PathProperties.parse("id,outgoingEdges(id,to),incomingEdges(id,from)");
    for (MnyNode mnyNode : list) {
      System.out.println(DB.json().toJson(mnyNode,path));

    }


  }
}
