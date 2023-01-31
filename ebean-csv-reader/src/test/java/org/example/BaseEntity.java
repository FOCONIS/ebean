package org.example;

import io.ebean.annotation.WhenCreated;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;

@MappedSuperclass
public abstract class BaseEntity {

  @Id
  long id;

  @Version
  long version;

  @WhenCreated
  Instant whenCreated;

  public long id() {
    return id;
  }

  public BaseEntity id(long id) {
    this.id = id;
    return this;
  }

  public long version() {
    return version;
  }

  public BaseEntity version(long version) {
    this.version = version;
    return this;
  }

  public Instant whenCreated() {
    return whenCreated;
  }

  public BaseEntity whenCreated(Instant whenCreated) {
    this.whenCreated = whenCreated;
    return this;
  }
}
