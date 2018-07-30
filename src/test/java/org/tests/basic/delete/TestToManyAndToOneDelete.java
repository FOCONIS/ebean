package org.tests.basic.delete;

import io.ebean.BaseTestCase;
import io.ebean.Ebean;
import org.junit.Test;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class TestToManyAndToOneDelete extends BaseTestCase {
  @Entity
  public static class EntityA {
    @Id
    private Short id;
    @OneToMany(cascade={CascadeType.ALL})
    private List<EntityB> manyB;
    @OneToOne(cascade ={})
    private EntityB oneB;
  }

  @Entity
  public static class EntityB {
    @Id
    private Short id;
  }

  @Test
  public void saveAndDelete() {
    EntityB add1 = new EntityB();
    EntityB add2 = new EntityB();
    EntityA cont = new EntityA();
    cont.manyB = new ArrayList<>();
    cont.manyB.add(add1);
    cont.manyB.add(add2);
    cont.oneB= add1;
    Ebean.save(cont);
    Ebean.delete(EntityA.class, cont.id);
    }
}
