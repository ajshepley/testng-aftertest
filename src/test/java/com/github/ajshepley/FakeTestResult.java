package com.github.ajshepley;

import org.testng.internal.TestResult;

class FakeTestResult extends TestResult {

  private final Object instance;

  public FakeTestResult(final Object instance) {
    this.instance = instance;
  }

  @Override
  public Object getInstance() {
    return this.instance;
  }
}
