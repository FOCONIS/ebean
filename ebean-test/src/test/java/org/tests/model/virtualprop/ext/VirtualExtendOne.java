package org.tests.model.virtualprop.ext;

import io.ebean.annotation.Formula;
import io.ebean.bean.extend.EntityExtension;
import io.ebean.bean.extend.ExtensionAccessor;
import io.ebean.bean.extend.ExtensionInfo;
import io.ebean.bean.extend.ExtensionManager;
import org.tests.model.virtualprop.VirtualBase;

import javax.persistence.*;

/**
 * @author Roland Praml, FOCONIS AG
 */

@Entity
public class VirtualExtendOne {

  @EntityExtension
  public static class VirtualBaseExtendOneOther {
    //public static final ExtensionInfo.Entry _extension_id = EntityExtension.extend(AbstractVirtualBase.class, VirtualBaseExtendOneOther.class);
    public static final ExtensionAccessor _extension_id = ExtensionManager.extend(VirtualBase.class, VirtualBaseExtendOneOther.class);

    @OneToOne(mappedBy = "base")
    private VirtualExtendOne virtualExtendOne;


    @Formula(select = "concat('Your name is ', ${ta}.data)")
    private String firstName;

    public static VirtualBaseExtendOneOther get(VirtualBase found) {
      return _extension_id.getExtension(found);
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
