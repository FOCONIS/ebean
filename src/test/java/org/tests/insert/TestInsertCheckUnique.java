package org.tests.insert;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Transaction;
import io.ebean.plugin.Property;
import io.ebeantest.LoggedSql;

import org.junit.Before;
import org.junit.Test;
import org.tests.model.draftable.Document;
import org.tests.model.m2m.MnyEdge;
import org.tests.model.m2m.MnyNode;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TestInsertCheckUnique extends BaseTestCase {

  @Before
  public void clearDb() {
    DB.find(Document.class).asDraft().where().contains("title", "UniqueKey").delete();
    DB.find(Document.class).asDraft().where().isNull("title").delete();
  }

  @Test
  public void insert_duplicateKey() {

    try (Transaction transaction = DB.beginTransaction()) {
      Document doc1 = new Document();
      doc1.setTitle("AUniqueKey_duplicateCheck");
      doc1.setBody("one");

      assertThat(DB.checkUniqueness(doc1)).isEmpty();
      doc1.save();

      Document doc2 = new Document();
      doc2.setTitle("AUniqueKey_duplicateCheck");
      doc2.setBody("clashes with doc1");

      assertThat(DB.checkUniqueness(doc2)).isEmpty();

      DB.getDefault().publish(doc1.getClass(), doc1.getId());

      assertThat(DB.checkUniqueness(doc2).toString()).contains("title");
    }
  }

  @Test
  public void insert_duplicateNull() {
    try (Transaction transaction = DB.beginTransaction()) {
      Document doc1 = new Document();
      doc1.setTitle(null);
      doc1.setBody("one");

      assertThat(DB.checkUniqueness(doc1)).isEmpty();
      doc1.save();

      Document doc2 = new Document();
      doc2.setTitle(null);
      doc2.setBody("clashes with doc1");

      assertThat(DB.checkUniqueness(doc2)).isEmpty();

      DB.getDefault().publish(doc1.getClass(), doc1.getId());

      assertThat(DB.checkUniqueness(doc2)).isEmpty();

      doc2.save();
      DB.getDefault().publish(doc2.getClass(), doc2.getId());
    }
  }

  @Test
  public  void testMultipleIndices() {
    MnyNode from = new MnyNode("from");
    DB.save(from);
    MnyNode to = new MnyNode("to");
    DB.save(to);

    MnyEdge edge = new MnyEdge();
    edge.setFrom(from);
    edge.setTo(to);
    DB.save(edge);

    LoggedSql.start();
    assertThat(DB.checkUniqueness(edge)).isEmpty();
    List<String> sql = LoggedSql.stop();
    assertThat(sql).hasSize(1);
    assertThat(sql.get(0)).startsWith("select t0.id from mny_edge t0 where t0.id <> ? and t0.from_id = ? and t0.to_id = ? limit 1");

    edge = new MnyEdge();
    edge.setFrom(from);
    edge.setTo(to);
    LoggedSql.start();
    assertThat(DB.checkUniqueness(edge)).extracting(Property::getName).containsExactly("from", "to");
    sql = LoggedSql.stop();
    assertThat(sql).hasSize(1);
    assertThat(sql.get(0)).startsWith("select t0.id from mny_edge t0 where t0.from_id = ? and t0.to_id = ? limit 1");
  }

  @Test
  public void example() {

    try (Transaction transaction = DB.beginTransaction()) {
      Document doc1 = new Document();
      doc1.setTitle("One flew over the cuckoo's nest");
      doc1.setBody("one");
      doc1.save();

      DB.getDefault().publish(doc1.getClass(), doc1.getId());

      Document doc2 = new Document();
      doc2.setTitle("One flew over the cuckoo's nest");
      doc2.setBody("clashes with doc1");

      Set<Property> properties = DB.checkUniqueness(doc2);
      if (properties.isEmpty()) {
        // it is unique ... carry on
      } else {
        // build a user friendly message
        // to return message back to user

        String uniqueProperties = properties.toString();

        StringBuilder msg = new StringBuilder();

        properties.forEach((it)-> {
          Object propertyValue = it.getVal(doc2);
          String propertyName = it.getName();
          msg.append(" property["+propertyName+"] value["+propertyValue+"]");
        });

        System.out.println("uniqueProperties > "+uniqueProperties);
        System.out.println("      custom msg > " + msg.toString());

      }


      assertThat(DB.checkUniqueness(doc2).toString()).contains("title");
    }
  }
}
