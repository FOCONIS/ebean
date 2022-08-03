package org.tests.model.virtualprop.ext;

import io.ebean.bean.EntityBean;
import io.ebean.bean.ExtensionInfo;
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
    public static final int _extension_id =       ExtensionInfo.extend(VirtualBase.class, VirtualBaseExtendManyToMany.class);

    @ManyToMany
    @JoinTable(name = "kreuztabelle")
    private List<VirtualExtendManyToMany> virtualExtendManyToManys;

    public static VirtualBaseExtendManyToMany get(VirtualBase found) {
      return (VirtualBaseExtendManyToMany) found._ebean_getExtension(_extension_id, ((EntityBean)found)._ebean_getIntercept());
    }


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
