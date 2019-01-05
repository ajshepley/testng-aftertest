package com.github.ajshepley;

import org.testng.IClass;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;

public class TestClassImpl implements IClass {

  private final Class<?> realClass;

  public TestClassImpl(final Class<?> realClass) {
    this.realClass = realClass;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public XmlTest getXmlTest() {
    return null;
  }

  @Override
  public XmlClass getXmlClass() {
    return null;
  }

  @Override
  public String getTestName() {
    return null;
  }

  @Override
  public Class<?> getRealClass() {
    return this.realClass;
  }

  @Override
  public Object[] getInstances(final boolean create) {
    return new Object[0];
  }

  @Override
  public int getInstanceCount() {
    return 0;
  }

  @Override
  public long[] getInstanceHashCodes() {
    return new long[0];
  }

  @Override
  public void addInstance(final Object instance) {

  }
}
