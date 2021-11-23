package org.tests.model.basic;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.joda.time.DateTime;

@Entity
public class MDateTime {

  @Id
  private Integer id;

  @Nullable
  private LocalTime localTime;

  @Nullable
  private LocalDateTime localDateTime;

  @Nullable
  private LocalDate localDate;

  @Nullable
  private OffsetDateTime offsetDateTime;

  @Nullable
  private ZonedDateTime zonedDateTime;
  
  @Nullable
  private YearMonth yearMonth;
  
  @Nullable
  private MonthDay monthDay;
  
  @Nullable
  private Year year;

  @Nullable
  private Instant instant;

  @Nullable
  private Calendar calendar;

  @Nullable
  private Timestamp timestamp;
  
  @Nullable
  private java.sql.Date sqlDate;

  @Nullable
  private java.sql.Time sqlTime;

  @Nullable
  private java.util.Date utilDate;

  @Nullable
  private org.joda.time.DateTime jodaDateTime;

  @Nullable
  private org.joda.time.LocalDateTime jodaLocalDateTime;

  @Nullable
  private org.joda.time.LocalDate jodaLocalDate;

  @Nullable
  private org.joda.time.LocalTime jodaLocalTime;

  @Nullable
  private org.joda.time.DateMidnight jodaDateMidnight;

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

  public OffsetDateTime getOffsetDateTime() {
    return offsetDateTime;
  }

  public void setOffsetDateTime(OffsetDateTime offsetDateTime) {
    this.offsetDateTime = offsetDateTime;
  }

  public ZonedDateTime getZonedDateTime() {
    return zonedDateTime;
  }

  public void setZonedDateTime(ZonedDateTime zonedDateTime) {
    this.zonedDateTime = zonedDateTime;
  }

  public YearMonth getYearMonth() {
    return yearMonth;
  }

  public void setYearMonth(YearMonth yearMonth) {
    this.yearMonth = yearMonth;
  }

  public MonthDay getMonthDay() {
    return monthDay;
  }

  public void setMonthDay(MonthDay monthDay) {
    this.monthDay = monthDay;
  }

  public Year getYear() {
    return year;
  }

  public void setYear(Year year) {
    this.year = year;
  }

  public Instant getInstant() {
    return instant;
  }

  public void setInstant(Instant instant) {
    this.instant = instant;
  }

  public Calendar getCalendar() {
    return calendar;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public java.sql.Date getSqlDate() {
    return sqlDate;
  }

  public void setSqlDate(java.sql.Date sqlDate) {
    this.sqlDate = sqlDate;
  }

  public java.sql.Time getSqlTime() {
    return sqlTime;
  }

  public void setSqlTime(java.sql.Time sqlTime) {
    this.sqlTime = sqlTime;
  }

  public java.util.Date getUtilDate() {
    return utilDate;
  }

  public void setUtilDate(java.util.Date utilDate) {
    this.utilDate = utilDate;
  }

  public org.joda.time.DateTime getJodaDateTime() {
    return jodaDateTime;
  }

  public void setJodaDateTime(org.joda.time.DateTime jodaDateTime) {
    this.jodaDateTime = jodaDateTime;
  }

  public org.joda.time.LocalDateTime getJodaLocalDateTime() {
    return jodaLocalDateTime;
  }

  public void setJodaLocalDateTime(org.joda.time.LocalDateTime jodaLocalDateTime) {
    this.jodaLocalDateTime = jodaLocalDateTime;
  }

  public org.joda.time.LocalDate getJodaLocalDate() {
    return jodaLocalDate;
  }

  public void setJodaLocalDate(org.joda.time.LocalDate jodaLocalDate) {
    this.jodaLocalDate = jodaLocalDate;
  }

  public org.joda.time.LocalTime getJodaLocalTime() {
    return jodaLocalTime;
  }

  public void setJodaLocalTime(org.joda.time.LocalTime jodaLocalTime) {
    this.jodaLocalTime = jodaLocalTime;
  }

  public org.joda.time.DateMidnight getJodaDateMidnight() {
    return jodaDateMidnight;
  }

  public void setJodaDateMidnight(org.joda.time.DateMidnight jodaDateMidnight) {
    this.jodaDateMidnight = jodaDateMidnight;
  }

}
