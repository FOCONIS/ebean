package org.tests.model.basic;

import io.ebean.Model;
import io.ebean.annotation.Cache;
import io.ebean.annotation.DbJson;
import io.ebean.annotation.Identity;
import io.ebean.annotation.MutationDetection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import static io.ebean.annotation.IdentityGenerated.BY_DEFAULT;

/**
 * @author Jonas Pöhler, Foconis Analytics GmbH
 */
@Cache(enableQueryCache = true)
@Entity
@Table(name = "e_basic_json_cache")
public class EBasicJsonCache extends Model {

  @Id
  @Identity(generated = BY_DEFAULT)
  Integer id;

  String name;

  String description;

  @DbJson(mutationDetection = MutationDetection.SOURCE)
  List<String> tags = new ArrayList<>();

  @DbJson(mutationDetection = MutationDetection.DEFAULT)
  List<String> superTags = new ArrayList<>();

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

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public List<String> getSuperTags() {
    return superTags;
  }

  public void setSuperTags(List<String> superTags) {
    this.superTags = superTags;
  }
}
