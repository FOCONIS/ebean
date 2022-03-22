package misc.migration.v1_0;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import io.ebean.annotation.DbComment;
import io.ebean.annotation.History;
import io.ebean.annotation.Index;

@Table(name = "`table`")
@Entity
@History
public class ETable {

  @Column(name = "`index`")
  @DbComment("this is a comment")
  @Id
  private String index;

  @Column(name = "`from`")
  @Index
  private String from;

  @Column(name = "`to`")
  @Index(unique = true)
  private String to;

  @Column(name = "`varchar`")
  @Index(unique = true)
  private String varchar;

  @ManyToOne
  @JoinColumn(name = "`foreign`")
  ETable foreign;

  @OneToMany(mappedBy = "foreign")
  List<ETable> foreigns;

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public String getVarchar() {
    return varchar;
  }

  public void setVarchar(String varchar) {
    this.varchar = varchar;
  }

  public ETable getForeign() {
    return foreign;
  }

  public void setForeign(ETable foreign) {
    this.foreign = foreign;
  }

  public List<ETable> getForeigns() {
    return foreigns;
  }

}
