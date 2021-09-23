package org.tests.model.tevent;

import io.ebean.annotation.Aggregation;
import io.ebean.annotation.Formula;
import org.tests.model.tevent.CountFormulaComputation.Count;
import org.tests.model.tevent.WhenFormulaComputation.When;

import javax.persistence.*;
import java.util.List;

@Entity
public class TEventOne {

  public enum Status {
    AA,
    BB
  }

  @Id
  Long id;

  String name;

  Status status;

  @Version
  Long version;

  @OneToOne
  TEvent event;

  @Aggregation("max(version)")
  Long maxVersion;

  @Aggregation("count(logs.id)")
  Long count;

  @Aggregation("sum(logs.units)")
  Double totalUnits;

  @Aggregation("sum(logs.units * logs.amount)")
  Double totalAmount;

  @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
  List<TEventMany> logs;

  @Count("logs")
  Long customLogCount;

  @Formula(
    select = "coalesce(for_customLogFormula.child_count, 0)",
    join = "left join (select event_id, count(*) as child_count from tevent_many group by event_id) for_customLogFormula on for_customLogFormula.event_id = ${ta}.id"
  )
  Long customLogFormula;

  @When(field = "status", op = "=", compareValue = "0", then = "'AA'")
  @When(field = "status", op = "=", compareValue = "1", then = "'BB'")
  String computedStatusWhen;

  @Formula(select = "(CASE WHEN ${ta}.status = 0 THEN 'AA' WHEN ${ta}.status = 1 THEN 'BB' ELSE NULL END)")
  String computedStatusFormula;

  public TEventOne(String name, Status status) {
    this.name = name;
    this.status = status;
  }

  @Override
  public String toString() {
    return "id:" + id + " name:" + name + " status:" + status + " mv:" + maxVersion + " ct:" + count;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCount() {
    return count;
  }

  public Long getCustomLogCount() {
    return customLogCount;
  }

  public Long getCustomLogFormula() {
    return customLogFormula;
  }

  public String getComputedStatusWhen() {
    return computedStatusWhen;
  }

  public String getComputedStatusFormula() {
    return computedStatusFormula;
  }

  public Double getTotalUnits() {
    return totalUnits;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public Long getMaxVersion() {
    return maxVersion;
  }

  public String getName() {
    return name;
  }

  public Status getStatus() {
    return status;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public TEvent getEvent() {
    return event;
  }

  public void setEvent(TEvent event) {
    this.event = event;
  }

  public List<TEventMany> getLogs() {
    return logs;
  }

  public void setLogs(List<TEventMany> logs) {
    this.logs = logs;
  }
}
