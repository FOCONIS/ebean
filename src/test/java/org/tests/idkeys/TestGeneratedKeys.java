package org.tests.idkeys;

import io.ebean.BaseTestCase;
import io.ebean.Transaction;
import io.ebean.annotation.ForPlatform;
import io.ebean.annotation.Platform;
import io.ebean.config.dbplatform.IdType;
import io.ebeaninternal.api.SpiEbeanServer;
import org.junit.Test;
import org.tests.idkeys.db.GenKeyIdentity;
import org.tests.idkeys.db.GenKeySequence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class TestGeneratedKeys extends BaseTestCase {

  @Test
  @ForPlatform({Platform.H2, Platform.DB2, Platform.MYSQL, Platform.SQLSERVER})
  public void testSequence() throws SQLException {
    SpiEbeanServer server = spiEbeanServer();

    if (idType() != IdType.SEQUENCE) {
      throw new IllegalStateException();
      // only run this test when SEQUENCE is being used
//      return;
    }

    try (Transaction tx = server.beginTransaction()) {

      long sequenceStart = readSequenceValue(tx, GenKeySequence.SEQUENCE_NAME);

      GenKeySequence al = new GenKeySequence();
      al.setDescription("my description");
      server.save(al);


      long sequenceCurrent = readSequenceValue(tx, GenKeySequence.SEQUENCE_NAME);

      assertNotNull(al.getId());
      assertFalse(sequenceStart == sequenceCurrent);
      assertEquals(sequenceStart + 20, sequenceCurrent);

      // Test second entity
      GenKeySequence al2 = new GenKeySequence();
      al2.setDescription("my second description");
      server.save(al2);

      sequenceCurrent = readSequenceValue(tx, GenKeySequence.SEQUENCE_NAME);

      assertNotEquals(al.getId(), al2.getId());
      // current sequence number must not be incremented
      assertEquals(sequenceStart + 20, sequenceCurrent);
    }

  }

  private long readSequenceValue(Transaction tx, String sequence) throws SQLException {
    try (Statement stm = tx.getConnection().createStatement()) {
      ResultSet rs;
      
      switch (spiEbeanServer().getDatabasePlatform().getPlatform()) {
        case H2 :
          rs = stm.executeQuery("select currval('" + sequence + "')");
          rs.next();
          return rs.getLong(1);
          
        case DB2 :
          rs = stm.executeQuery("values previous value for " + sequence);
          rs.next();
          return rs.getLong(1);
          
        case MYSQL :
          rs = stm.executeQuery("select previous value for " + sequence);
          rs.next();
          return rs.getLong(1);
          
        default :
          throw new UnsupportedOperationException("reading sequence value from " + spiEbeanServer().getDatabasePlatform().getPlatform() + " is not supported.");
      }
    }
  }

  @Test
  public void testIdentity() throws SQLException {

    if (idType() != IdType.IDENTITY) {
      // only run this test when SEQUENCE is being used
      return;
    }

    try (Transaction tx = server().beginTransaction()) {

      GenKeyIdentity al = new GenKeyIdentity();
      al.setDescription("my description");
      server().save(al);

      // For JDBC batching we won't get the id until after
      // the batch has been flushed explicitly or via commit
      //assertNotNull(al.getId());

      tx.commit();

      assertNotNull(al.getId());
    }
  }

}
