package org.tests.modular;

import io.ebean.DB;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestModularity {

  @Test
  public void testFoo() {
    ModularA modularA = new ModularA();
    modularA.setContent("ooooooo");
    DB.save(modularA);
    ModularB modularB = new ModularB();
    modularB.setModularA(modularA);
    modularB.setValue("modB");
    DB.save(modularB);

    modularA = DB.find(ModularA.class, modularA.getId());
    assertThat(modularA).isNotNull();

    modularB = DB.find(ModularB.class, modularB.getId());
    assertThat(modularB).isNotNull();

    assertThat(DB.find(ModularA.class).where().eq("modularB.value", "modB").exists()).isTrue();

    DB.delete(modularA);

    assertThat(DB.find(ModularB.class, modularB.getId())).isNull();

  }

}
