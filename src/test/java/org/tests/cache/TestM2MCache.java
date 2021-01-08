package org.tests.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.tests.model.cache.M2MCacheChild;
import org.tests.model.cache.M2MCacheMaster;

import io.ebean.BaseTestCase;
import io.ebean.DB;
import io.ebean.Transaction;
import io.ebean.TxScope;

public class TestM2MCache extends BaseTestCase {

	@Test
	public void testM2MWithCache() throws Exception {
		M2MCacheChild cld = new M2MCacheChild();
		cld.setName("blah");
		cld.setId(1);
		DB.save(cld);
		M2MCacheMaster b = DB.find(M2MCacheMaster.class, 42);
		try (Transaction txn = DB.beginTransaction(TxScope.requiresNew())) {
			txn.putUserObject("persistAction", "Initialisierung");
			M2MCacheMaster cfg = new M2MCacheMaster();
			cfg.setId(42);
			cfg.getSet1().add(cld);
			cfg.getSet2().add(cld);
			DB.save(cfg);
			txn.commit();
			b = cfg;
		}
		assert b != null;
		M2MCacheMaster c = b;
		DB.save(c);


		M2MCacheMaster cfg3 = DB.find(M2MCacheMaster.class, 42);
		M2MCacheMaster cfg1 = DB.find(M2MCacheMaster.class, 42);

		cfg1.getSet1().size();
		cfg1.getSet2().size();
		assertThat(cfg1.getSet1().iterator().next().getName()).isEqualTo("blah");

	}
}
