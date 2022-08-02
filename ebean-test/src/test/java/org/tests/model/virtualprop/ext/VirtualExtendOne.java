package org.tests.model.virtualprop.ext;

import org.tests.model.virtualprop.VirtualBase;
import org.tests.model.virtualprop.VirtualEmbed;

import javax.persistence.*;

/**
 * @author Roland Praml, FOCONIS AG
 */

@Entity
public class VirtualExtendOne {

  @VirtualEmbed(value = VirtualBase.class)
  @Embeddable
  static class VirtualBaseExtendOneOther {
    @OneToOne(mappedBy = "base")
    private VirtualExtendOne virtualExtendOne;
  }
  @Id
  private int id;

  private String data;

  @PrimaryKeyJoinColumn
  @OneToOne(optional = false)
  private VirtualBase base;

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

  public VirtualBase getBase() {
    return base;
  }

  public void setBase(VirtualBase base) {
    this.base = base;
    this.id = base == null ? 0 : base.getId();
  }
}
