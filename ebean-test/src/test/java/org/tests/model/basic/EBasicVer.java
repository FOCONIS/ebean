package org.tests.model.basic;

import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.DbJson;
import io.ebean.annotation.Identity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static io.ebean.annotation.IdentityGenerated.BY_DEFAULT;

@Cache(enableQueryCache = true)
@Entity
@Table(name = "e_basicver")
public class EBasicVer extends Model {

  @Id @Identity(generated = BY_DEFAULT)
  Integer id;

  String name;

  String description;

  String other;

  @DbJson
  List<String> tags = new ArrayList<>();

  @Lob
  String superTags;

  @Version
  Timestamp lastUpdate;

  public EBasicVer(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public String getOther() {
    return other;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public Timestamp getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Timestamp lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getSuperTags() {
    return superTags;
  }

  public void setSuperTags(String superTags) {
    this.superTags = superTags;
  }
}
