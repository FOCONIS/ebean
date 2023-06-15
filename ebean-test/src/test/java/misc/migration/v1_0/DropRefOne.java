package misc.migration.v1_0;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "migtest_drop_ref_one")
public class DropRefOne {

  @Id
  Integer id;

  @ManyToOne(cascade = {})
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
