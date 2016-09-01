package com.avaje.tests.softdelete;

import static org.assertj.core.api.StrictAssertions.assertThat;

import org.junit.Test;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.tests.model.softdelete.ReferencedEntity;
import com.avaje.tests.model.softdelete.ReferencedEntityNoSDQ;
import com.avaje.tests.model.softdelete.ReferrerEntity;
import com.avaje.tests.model.softdelete.ReferrerEntityNoSDQ;

public class TestSoftDeleteReferenceEntity extends BaseTestCase {

	@Test
	public void testFindWithSDQ() {
		long id = runWithA1();
		runWithA2(id);
	}

	private long runWithA1() {
		ReferencedEntity referenced = new ReferencedEntity("Reference", "With includeInQueries");
		Ebean.save(referenced);
		ReferrerEntity ent = new ReferrerEntity(referenced, "Referrer", 44497);
		Ebean.save(ent);
		ent = new ReferrerEntity(referenced, "Referrer id 2", 11213);
		Ebean.save(ent);
		return ent.getId();
	}

	private void runWithA2(final long id) {
		ReferrerEntity bean = Ebean.find(ReferrerEntity.class).setId(id).findUnique();
		assertThat(bean).isNotNull();
		ReferencedEntity reference = bean.getReference();
		assertThat(reference).isNotNull();
		System.out.println("reference: Name=" + reference.getName() + " / Desc=" + reference.getDescription());
		Ebean.delete(reference);
		ReferencedEntity ref2 = Ebean.find(ReferencedEntity.class).setId(reference.getId()).findUnique();
		assertThat(ref2).isNull();
		bean = Ebean.find(ReferrerEntity.class).setId(id).findUnique();
		assertThat(bean).isNotNull();
		reference = bean.getReference();
		// Originally, I had expected reference to be NULL here, but we get a
		// dummy entity with only id set.
		// Further properties are then loaded via lazy loading. So:
		assertThat(reference).isNotNull();
		System.out.println("Soft-deleted reference object is not NULL");
		assertThat(reference.getDeleted()).isTrue();
	}

	@Test
	public void testFindWithoutSDQ() {
		long id = runWithoutA1();
		runWithoutA2(id);
	}

	private long runWithoutA1() {
		ReferencedEntityNoSDQ referenced = new ReferencedEntityNoSDQ("Reference", "Without includeInQueries");
		Ebean.save(referenced);
		ReferrerEntityNoSDQ ent = new ReferrerEntityNoSDQ(referenced, "Referrer", 86243);
		Ebean.save(ent);
		ent = new ReferrerEntityNoSDQ(referenced, "Referrer id 2", 23209);
		Ebean.save(ent);
		return ent.getId();
	}

	private void runWithoutA2(final long id) {
		ReferrerEntityNoSDQ bean = Ebean.find(ReferrerEntityNoSDQ.class).setId(id).findUnique();
		assertThat(bean).isNotNull();
		ReferencedEntityNoSDQ reference = bean.getReference();
		assertThat(reference).isNotNull();
		System.out.println("reference: Name=" + reference.getName() + " / Desc=" + reference.getDescription());

		Ebean.delete(reference);
		ReferencedEntityNoSDQ ref2 = Ebean.find(ReferencedEntityNoSDQ.class).setId(reference.getId()).findUnique();
		assertThat(ref2).isNotNull();
		ref2 = Ebean.find(ReferencedEntityNoSDQ.class).setId(reference.getId()).where().eq("deleted", false)
				.findUnique();
		assertThat(ref2).isNull();
		ref2 = Ebean.find(ReferencedEntityNoSDQ.class).setId(reference.getId()).where().eq("deleted", true)
				.findUnique();
		assertThat(ref2).isNotNull();

		bean = Ebean.find(ReferrerEntityNoSDQ.class).setId(id).findUnique();
		assertThat(bean).isNotNull();
		reference = bean.getReference();
		assertThat(reference).isNotNull();
		System.out.println("Round 2: Soft-deleted reference object is not NULL");
		System.out.println("Desc=" + reference.getDescription());
	}
}
