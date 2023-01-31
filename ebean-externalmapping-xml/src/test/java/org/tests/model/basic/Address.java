package org.tests.model.basic;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "o_address")
public class Address extends BasicDomain {
  String line1;
  String line2;
  String city;
//  @ManyToOne
//  Country country;
}
