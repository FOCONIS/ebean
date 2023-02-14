package misc.migration.v1_2;

import io.ebean.annotation.Index;
import io.ebean.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;

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
  }

  @Id
  Integer id;

  Status status;

  @DbDefault("N")
  @NotNull
  Status status2;

  @Size(max=127)
  String name;

  @Size(max=127)
  String description;

  @Lob
  @Column(columnDefinition = "db2;blob(64M);")
  File descriptionFile;

  @DbJson
  @Column(columnDefinition = "db2;clob(16K) inline length 500 compact;")
  List<String> jsonList;

  @NotNull
  @DbDefault("X")
  @Column(columnDefinition = "db2;clob(16K) inline length 500 compact;")
  String aLob;

  Timestamp someDate;

  boolean old_boolean;

  Boolean old_boolean2;

  @ManyToOne
  ERef eref;


  // test add & remove indices
  @Index
  @Size(max=127)
  String indextest1;

  @Index(unique = true)
  @Size(max=127)
  String indextest2;

  @Size(max=127)
  String indextest3;

  @Size(max=127)
  String indextest4;

  @Index(unique = false)
  @Size(max=127)
  String indextest5;

  @Index(unique = true)
  @Size(max=127)
  String indextest6;

  @NotNull
  @DbDefault("23")
  int user_id;

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

}