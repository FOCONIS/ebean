package org.tests.model.basic;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "o_product")
public class Product extends BasicDomain {
  String sku;
  String name;
}
