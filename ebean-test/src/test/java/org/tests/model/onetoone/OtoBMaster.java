package org.tests.model.onetoone;

import javax.persistence.*;

@Entity
public class OtoBMaster {

  @Id
  Long id;

  String name;

  // BMaster and BChild are both not optional. Note, the DDL contains only a foreign key on oto_bchild
  // alter table oto_bchild add constraint fk_oto_bchild_master_id foreign key (master_id) references oto_bmaster (id) on delete restrict on update restrict;
  // So you cannot save a child without it's master, but you can save a master without child (but this would be a violation of the 'optional' contract)
  @OneToOne(cascade = CascadeType.ALL, mappedBy = "master", fetch = FetchType.LAZY)
  OtoBChild child;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OtoBChild getChild() {
    return child;
  }

  public void setChild(OtoBChild child) {
    this.child = child;
  }

}
