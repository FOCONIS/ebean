package org.tests.model.virtualprop.ext;

import io.ebean.bean.NotEnhancedException;
import io.ebean.bean.extend.EntityExtension;
import org.tests.model.virtualprop.AbstractVirtualBase;
import org.tests.model.virtualprop.VirtualBaseA;

import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * This class will add the field 'ext' to 'VirtualBaseA' by EntityExtension
 */
@EntityExtension(VirtualBaseA.class)
public class Extension3 {

  private String ext;

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public static Extension3 get(VirtualBaseA base) {
    throw new NotEnhancedException();
  }
}
