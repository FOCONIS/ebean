package org.tests.model.basic;

import io.ebean.annotation.Cache;
import io.ebean.annotation.DocEmbedded;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "o_order_detail")
public class OrderDetail extends BasicDomain {

  @ManyToOne(optional = false)
  Order order;

  Integer orderQty;
  Integer shipQty;
  Double unitPrice;

  @ManyToOne
  Product product;

}
