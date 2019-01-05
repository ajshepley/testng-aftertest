package com.github.ajshepley;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(SafeAfterTestMethodListener.class)
public class ExampleTest {

  @BeforeMethod(alwaysRun = true)
  public void setup() {
    System.out.println("Running test setup.");
  }

  @Test
  public void testFail() {
    assertEquals(1, 2);
  }

  @Test
  public void testSuccess() {
    assertTrue(true);
  }

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
  public void nullMethod() {
    System.out.println("Executing nullMethod.");
  }

  @SafeAfterTestMethod(priority = 2)
  public void nullMethod2() {
    System.out.println("Executing nullMethod2.");
  }

  @SafeAfterTestMethod
  public void nullMethod3() {
    System.out.println("Executing nullMethod3.");
  }
}
