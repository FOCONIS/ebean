package org.tests.model.json;

import javax.persistence.DiscriminatorValue;

import io.ebean.annotation.DocStore;

@DocStore
@DiscriminatorValue("A")
public class JsonModelA extends JsonModel {

}
