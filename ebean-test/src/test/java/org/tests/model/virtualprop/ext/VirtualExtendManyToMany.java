package org.tests.model.virtualprop.ext;

import io.ebean.bean.NotEnhancedException;
import io.ebean.bean.extend.EntityExtension;
import io.ebean.bean.extend.ExtensionAccessor;
import io.ebean.bean.extend.ExtensionManager;
import org.tests.model.basic.Customer;
import org.tests.model.virtualprop.AbstractVirtualBase;
import org.tests.model.virtualprop.VirtualBase;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * @author Roland Praml, FOCONIS AG
 */
@Entity
public class VirtualExtendManyToMany {
  @Id
  private int id;

  private String data;

  @EntityExtension(AbstractVirtualBase.class)
  public static class VirtualBaseExtendManyToMany {

    @ManyToMany
    @JoinTable(name = "kreuztabelle")
    private List<VirtualExtendManyToMany> virtualExtendManyToManys;

    public List<VirtualExtendManyToMany> getVirtualExtendManyToManys() {
      return virtualExtendManyToManys;
    }

    // TODO: Bytecode in Enhancer
    public static final ExtensionAccessor _extension_id = ExtensionManager.extend(AbstractVirtualBase.class, VirtualBaseExtendManyToMany.class);


    public  static VirtualBaseExtendManyToMany get(VirtualBase found) {
      throw new NotEnhancedException();
//
     // return _extension_id.getExtension(found);
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
