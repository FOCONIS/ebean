package com.avaje.tests.idkeys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.config.dbplatform.IdType;
import com.avaje.ebeaninternal.api.SpiEbeanServer;
import com.avaje.tests.idkeys.db.GenKeyIdentity;
import com.avaje.tests.idkeys.db.GenKeySequence;

/**
 * Test various key generation strategies.
 *
 * Notice: The strategy {@link javax.persistence.GenerationType#AUTO} is not easy to test as it is database dependent
 * which of the strategies will be used. 
 */
public class TestGeneratedKeys extends BaseTestCase
{
    /**
     * Test key generation using a sequence.
     * This should increment the used sequence by one.
     * EBean should construct a select statement which uses the configured sequence to get the next value
     * <p/>
     * Notice: Actually, this test depends on H2 as used database as we are going to fetch the current value from the sequence
     */
    @Test
    public void testSequence() throws SQLException
    {
    	SpiEbeanServer server = (SpiEbeanServer)server();
    	IdType idType = server.getDatabasePlatform().getDbIdentity().getIdType();
    	String platformName = server.getDatabasePlatform().getName();
    	assumeTrue("only run this test when SEQUENCE is being used", IdType.SEQUENCE.equals(idType));
    	assumeTrue("readSequenceValue is H2 specific.", isH2()); 
    	
        Transaction tx = server().beginTransaction();

        long sequenceStart = readSequenceValue(tx, GenKeySequence.SEQUENCE_NAME);        
        
        GenKeySequence al = new GenKeySequence();
        al.setDescription("my description");
        server().save(al);

        
        long sequenceCurrent = readSequenceValue(tx, GenKeySequence.SEQUENCE_NAME);

        assertNotNull(al.getId());
        assertFalse("sequence advanced", sequenceStart == sequenceCurrent);
        assertEquals("sequence advanced by 20", sequenceStart + 20, sequenceCurrent);
        
    }

    private long readSequenceValue(Transaction tx, String sequence) throws SQLException
    {
        Statement stm = null;
        try
        {
            stm = tx.getConnection().createStatement();
            ResultSet rs = stm.executeQuery("select currval('" + sequence + "')");
            rs.next();

            return rs.getLong(1);
        }
        finally
        {
            if (stm != null)
            {
                try
                {
                    stm.close();
                }
                catch (SQLException e)
                {
                    ;
                }
            }
        }
    }

    /**
     * This test just checks if a generated value has been passed back from the database.
     * EBean should simply fetch the generated key after inserting.
     */
    @Test    
    public void testIdentity() throws SQLException
    {

    	SpiEbeanServer server = (SpiEbeanServer)server();
    	IdType idType = server.getDatabasePlatform().getDbIdentity().getIdType();
    	
    	if (!IdType.IDENTITY.equals(idType)){
    		// only run this test when SEQUENCE is being used
    		return;
    	}

        Transaction tx = server().beginTransaction();

        GenKeyIdentity al = new GenKeyIdentity();
        al.setDescription("my description");
        server().save(al);

        // For JDBC batching we won't get the id until after
        // the batch has been flushed explicitly or via commit
        //assertNotNull(al.getId());

        tx.commit();
        
        assertNotNull(al.getId());

    }

    /**
     * This should use the next value using max(col)+1 on the table
     *
     * AFAIK this is not supported by EBean yet
     public void testTable() throws SQLException
     {
         EbeanServer server = Ebean.getServer(null);
         Transaction tx = server.beginTransaction();

         GenKeyTable al = new GenKeyTable();
         al.setDescription("my description");
         server.save(al);

         assertNotNull(al.getId());
         assertEquals(0L, al.getId().longValue());

         al = new GenKeyTable();
         al.setDescription("my description - another object");
         server.save(al);

         assertNotNull(al.getId());
         assertEquals(1L, al.getId().longValue());

         tx.commit();
     }
     */
    
    @After
    public void tearDown() {
      Transaction tx = server().currentTransaction();
      if (tx != null && tx.isActive()) {
        // transaction left running after the test, rollback it to make
        // the environment ready for the next test
        tx.rollback();
      }
    }
}