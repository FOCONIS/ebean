package misc.migration.v1_1;

import io.ebean.Platform;
import io.ebean.annotation.DdlInfo;
import io.ebean.annotation.DdlScript;
import io.ebean.annotation.EnumValue;
import io.ebean.annotation.Index;
import io.ebean.annotation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "migtest_e_basic")
public class EBasic {

  public enum Status {
    @EnumValue("N")
    NEW,

    @EnumValue("A")
    ACTIVE,

    @EnumValue("I")
    INACTIVE,
    
    @EnumValue("?")
    DONT_KNOW,
  }

  public enum Progress {
    @EnumValue("0")
    START,

    @EnumValue("1")
    RUN,

    @EnumValue("2")
    END
  }
  
  @Id
  Integer id;

  @NotNull
  @DdlInfo(defaultValue="A")
  Status status;

  @Index(unique = true)
  String name;

  @DdlInfo(preAlter = @DdlScript("-- clean up uniqueness"))
  @Column(unique = true)
  String description;

  @NotNull
  @DdlInfo(defaultValue="'2000-01-01T00:00:00'")
  Timestamp someDate;
  
  @NotNull
  @DdlInfo(defaultValue="foo")
  String newStringField;

  @NotNull
  @DdlInfo(defaultValue="true", postAdd = @DdlScript("update ${table} set ${column} = old_boolean"))
  Boolean newBooleanField;

  @NotNull
  @DdlInfo(defaultValue="true")
  boolean newBooleanField2;
  
  String indextest1;
  
  String indextest2;
  
  @Index
  String indextest3;
  
  @Index(unique = true)
  String indextest4;
  
  @Index(unique = true)
  String indextest5;
  
  @Index(unique = false)
  String indextest6;
  
  @NotNull
  @DdlInfo(defaultValue = "0")
  Progress progress;
  
  @NotNull
  @ManyToOne
  EUser user;
  
  public EBasic() {

  }

  public EBasic(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Timestamp getSomeDate() {
    return someDate;
  }

  public void setSomeDate(Timestamp someDate) {
    this.someDate = someDate;
  }

  public String getNewStringField() {
    return newStringField;
  }
  
  public void setNewStringField(String newStringField) {
    this.newStringField = newStringField;
  }
}
