package org.example.domain

import io.ebean.Model
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Version

@Entity
class FooEntity(name: String) : Model() {

  @Id
  var id: Long = 0

  var name: String = name

  @Version
  var version: Long = 0
}
