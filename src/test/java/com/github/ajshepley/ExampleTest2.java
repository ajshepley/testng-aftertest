package com.github.ajshepley;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class ExampleTest2 {

  @Test
  public void testSuccess1() {
    assertTrue(true);
  }

  @Test
  public void testSuccess2() {
    assertTrue(true);
  }

  @Test
  public void testSuccess3() {
    assertTrue(true);
  }

  @SafeAfterTestMethod(priority = 1)
  public void anAfterMethod1() {
    System.out.println("Executing ExampleTest2 afterMethod 1");
  }

  @SafeAfterTestMethod(priority = 2)
  public void anAfterMethod2() {
    System.out.println("Executing ExampleTest2 afterMethod 2");
  }

  @SafeAfterTestMethod
  public void anAfterMethod3() {
    System.out.println("Executing ExampleTest2 afterMethod 3");
  }
}
