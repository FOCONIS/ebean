package org.tests.model.basic;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "o_order")
public class Order extends BasicDomain {

  public enum Status {
    NEW,
    APPROVED,
    SHIPPED,
    COMPLETE
  }

  @Enumerated(value = EnumType.ORDINAL)
  Status status = Status.NEW;
  LocalDate orderDate;
  LocalDate shipDate;

  @ManyToOne(cascade = CascadeType.PERSIST)
  Customer customer;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
  @OrderBy("id asc, orderQty asc, cretime desc")
  List<OrderDetail> details;

}
