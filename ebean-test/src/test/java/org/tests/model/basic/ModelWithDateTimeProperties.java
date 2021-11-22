package org.tests.model.basic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ModelWithDateTimeProperties{

  @Id
  Integer id;
  
  @Nullable
  LocalTime localTime;
  
  @Nullable
  LocalDateTime localDateTime;
  
  @Nullable
  LocalDate localDate;
  
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public LocalTime getLocalTime() {
    return localTime;
  }

  public void setLocalTime(LocalTime localTime) {
    this.localTime = localTime;
  }

  public LocalDateTime getLocalDateTime() {
    return localDateTime;
  }

  public void setLocalDateTime(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
  }

  public LocalDate getLocalDate() {
    return localDate;
  }

  public void setLocalDate(LocalDate localDate) {
    this.localDate = localDate;
  }

}
