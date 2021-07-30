package org.tests.model.m2m;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import io.ebean.annotation.Where;

@Entity
public class ReportResult {
  
  @Id
  private UUID id;

  String name;
  
  @ManyToMany(cascade = CascadeType.REFRESH)
  @JoinTable(name = "notification_target",
          joinColumns = @JoinColumn(name = "target_uuid", referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "notification_id", referencedColumnName = "id"))
  @Where(clause = "${ta}.ref_table_name='${dbTableName}'")
  private List<Notification> notifications;
  
  @ManyToMany(cascade = CascadeType.REMOVE)
  private Set<Role> readAuthorization = new LinkedHashSet<Role>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public List<Notification> getNotifications() {
    return notifications;
  }

  public Set<Role> getReadAuthorization() {
    return readAuthorization;
  }
}
