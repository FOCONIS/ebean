package com.avaje.tests.lifecycle;

public class SimpleContext {
  private String contextValue;
  
  public SimpleContext(String contextValue) {
    this.contextValue = contextValue;
  }

  public String getContextValue() {
    return contextValue;
  }
}
