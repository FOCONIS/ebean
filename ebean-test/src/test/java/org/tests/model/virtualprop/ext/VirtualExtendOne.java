package org.tests.model.virtualprop.ext;

import io.ebean.annotation.Formula;
import io.ebean.bean.EntityBean;
import org.tests.model.virtualprop.VirtualBase;
import org.tests.model.virtualprop.VirtualEmbed;

import javax.persistence.*;

/**
 * @author Roland Praml, FOCONIS AG
 */

@Entity
public class VirtualExtendOne {

  @VirtualEmbed(value = VirtualBase.class)
  @Entity
  public static class VirtualBaseExtendOneOther {
    public static int _extension_id = -1;
    @OneToOne(mappedBy = "base")
    private VirtualExtendOne virtualExtendOne;

    @Formula(select = "concat('Your name is ', ${ta}.data)")
    private String firstName;

    public static VirtualBaseExtendOneOther get(VirtualBase found) {
      return (VirtualBaseExtendOneOther) found._ebean_getExtension(_extension_id, ((EntityBean)found)._ebean_getIntercept());
    }

    /*public static VirtualBaseExtendOneOther wrap(VirtualBase b) {

    }*/

    public VirtualExtendOne getVirtualExtendOne() {
      return virtualExtendOne;
    }

    public void setVirtualExtendOne(VirtualExtendOne virtualExtendOne) {
      this.virtualExtendOne = virtualExtendOne;
    }

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }
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
