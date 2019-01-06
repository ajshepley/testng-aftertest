package com.github.ajshepley;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

// Note that the listener only needs to be present on a single unit test within the executed suite
// to look for and invoke @SafeAfterTestMethod annotated methods.
@Listeners(SafeAfterTestMethodListener.class)
@SuppressWarnings("unused")
public class ExampleTest {

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

  @SafeAfterTestMethod(priority = 3)
  public void anAfterMethod1() {
    System.out.println("Executing ExampleTest afterMethod 1");
  }

  @SafeAfterTestMethod(priority = 1)
  public void anAfterMethod2() {
    System.out.println("Executing ExampleTest afterMethod 2");
  }

  @SafeAfterTestMethod(priority = 2)
  public void anAfterMethod3() {
    System.out.println("Executing ExampleTest afterMethod 3");
  }
}
