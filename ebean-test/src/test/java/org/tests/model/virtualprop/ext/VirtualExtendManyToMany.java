package org.tests.model.virtualprop.ext;

import io.ebean.bean.EntityBean;
import io.ebean.bean.EntityExtension;
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

  @Entity
  public static class VirtualBaseExtendManyToMany implements EntityExtension {
    public static final ExtensionInfo.Entry _extension_id = EntityExtension.extend(VirtualBase.class, VirtualBaseExtendManyToMany.class);

    @ManyToMany
    @JoinTable(name = "kreuztabelle")
    private List<VirtualExtendManyToMany> virtualExtendManyToManys;

    public static VirtualBaseExtendManyToMany get(VirtualBase found) {
      return _extension_id.getExtension(found);
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
