package org.tests.model.docstore;

import io.ebean.annotation.DocStore;
import org.tests.model.basic.Product;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ManyToOne;

@DocStore
@DiscriminatorValue("PR")
public class ProductReport extends Report {

  @ManyToOne
  private Product product;

  public Product getProduct() {
    return product;
  }
  public void setProduct(Product product) {
    this.product = product;
  }
}
