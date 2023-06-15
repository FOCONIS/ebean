package misc.migration.v1_2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "migtest_drop_ref_one_to_one")
public class DropRefOneToOne {

  @Id
  Integer id;

  @OneToOne(cascade = {})
  DropMain parent;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public DropMain getParent() {
    return parent;
  }

  public void setParent(DropMain parent) {
    this.parent = parent;
  }
}
