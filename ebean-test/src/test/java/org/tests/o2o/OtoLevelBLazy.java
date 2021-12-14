package org.tests.o2o;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class OtoLevelBLazy {

  @Id
  private Long id;

  private String name;
  
  
  @ManyToMany()
  private List<OtoLevelC> c;
  
  
//  @ManyToOne()
//  private OtoLevelA a2;
  

  @OneToOne()
  private OtoLevelALazy a;
  
  @Lob
  private String blob;

  public OtoLevelBLazy(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OtoLevelALazy getA() {
    return a;
  }

  public void setA(OtoLevelALazy a) {
    this.a = a;
  }
  
  public String getName() {
	return name;
}
  
  public void setName(String name) {
	this.name = name;
  }
  
 public List<OtoLevelC> getC() {
	return c;
 }
 
	public final void _ebean_onPersistTrigger(String trt) {
		try {
		
		recalc();
		} catch(Throwable th) {
			th.printStackTrace();
		}
	}
	protected void recalc() {
			//this.getName();
		this.getBlob();
	}
//	
//	public void setA2(OtoLevelA a2) {
//		this.a2 = a2;
//	}
//	
//	public OtoLevelA getA2() {
//		return a2;
//	}
	
	public String getBlob() {
		return blob;
	}
	
	public void setBlob(String blob) {
		this.blob = blob;
	}
 
}
