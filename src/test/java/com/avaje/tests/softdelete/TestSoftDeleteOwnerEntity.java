package com.avaje.tests.softdelete;

import static org.assertj.core.api.StrictAssertions.assertThat;

import org.junit.Test;

import com.avaje.ebean.BaseTestCase;
import com.avaje.ebean.Ebean;
import com.avaje.tests.model.softdelete.OwnedEntity;
import com.avaje.tests.model.softdelete.OwnerEntity;

public class TestSoftDeleteOwnerEntity extends BaseTestCase {

	@Test
	public void testSoftDeleteOwner() {
		CountingBeanPersistController.reset();
		long id = prepSoftDelete();
		doSoftDelete(id);
		CountingBeanPersistController.printLists(false);
		CountingBeanPersistController.assertCounts();
		postSoftDeleteInspect(id);
	}

	private long prepSoftDelete() {
		OwnerEntity owner = new OwnerEntity("Parent");
		owner.addChild(new OwnedEntity("Child"));
		Ebean.save(owner);
		return owner.getId();
	}

	private void doSoftDelete(final long id) {
		OwnerEntity bean = Ebean.find(OwnerEntity.class).setId(id).findUnique();
		assertThat(bean).isNotNull();
		assertThat(bean.getChildren().size()).isEqualTo(1);
		Ebean.delete(bean);
	}

	private void postSoftDeleteInspect(final long id) {
		OwnerEntity bean = Ebean.find(OwnerEntity.class).setId(id).setIncludeSoftDeletes().findUnique();
		assertThat(bean).isNotNull();
		assertThat(bean.isDeleted()).isTrue();
		assertThat(bean.getChildren().size()).isEqualTo(1);
		assertThat(bean.getVersion()).isEqualTo(2);
		assertThat(Ebean.find(OwnedEntity.class).findList().isEmpty()).isTrue();
		assertThat(Ebean.find(OwnedEntity.class).setIncludeSoftDeletes().findList().isEmpty()).isFalse();
		OwnedEntity child = bean.getChildren().get(0);
		assertThat(child).isNotNull();
		assertThat(child.isDeleted()).isTrue();
		assertThat(child.getParent()).isEqualTo(bean);
		assertThat(child.getVersion()).isEqualTo(2); // Should be 2. But is 1 at the moment.
	}
}
