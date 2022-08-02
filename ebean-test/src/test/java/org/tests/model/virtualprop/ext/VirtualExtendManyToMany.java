package org.tests.model.virtualprop.ext;

import org.tests.model.virtualprop.VirtualBase;
import org.tests.model.virtualprop.VirtualEmbed;

import javax.persistence.*;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
@Entity
public class VirtualExtendManyToMany {
  @Id
  private int id;

  private String data;

  @VirtualEmbed(VirtualBase.class)
  @Entity
  @Table(name = "virtual_base123")
  public static class VirtualBaseExtendManyToMany {
    @ManyToMany
    @JoinTable(name = "kreuztabelle")
    private List<VirtualExtendManyToMany> virtualExtendManyToManys;
  }

  @ManyToMany(mappedBy = "virtualExtendManyToManys")
  private List<VirtualBase> bases;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public List<VirtualBase> getBases() {
    return bases;
  }

  public void setBases(List<VirtualBase> bases) {
    this.bases = bases;
  }
}
