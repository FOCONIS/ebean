package misc.migration.v1_0;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * @author Jonas P&ouml;hler, FOCONIS AG
 */
@Entity
@Table(name = "drop_main")
public class DropMain {

  @Id
  Integer id;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
  List<DropRefOne> refsOne;

  @ManyToMany(cascade = CascadeType.ALL)
  List<DropRefMany> refsMany;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public List<DropRefOne> getRefsOne() {
    return refsOne;
  }

  public void setRefsOne(List<DropRefOne> refsOne) {
    this.refsOne = refsOne;
  }

  public List<DropRefMany> getRefsMany() {
    return refsMany;
  }

  public void setRefsMany(List<DropRefMany> refsMany) {
    this.refsMany = refsMany;
  }
}
