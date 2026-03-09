package org.tests.batchload;

import io.ebean.DB;
import io.ebean.xtest.BaseTestCase;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.tests.model.basic.UUOne;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestLazyLoadNonExistantBean extends BaseTestCase {

  @Test
  public void testSimple() {

    UUID uuid = UUID.randomUUID();
    UUOne one = DB.reference(UUOne.class, uuid);

    assertThatThrownBy(one::getName).isInstanceOf(EntityNotFoundException.class).hasMessageContaining("Bean not found during lazy load or refresh.");
    assertThatThrownBy(one::getName).isInstanceOf(EntityNotFoundException.class).hasMessageContaining("Bean not found during lazy load or refresh.");
  }
}
