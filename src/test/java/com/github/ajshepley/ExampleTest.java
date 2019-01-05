package com.github.ajshepley;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Random;
import javax.xml.ws.Action;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(SafeAfterTestListener.class)
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

//  @AfterMethod
  public void afterMethod() {
    if (new Random().nextBoolean()) {
      throw new NullPointerException();
    }
  }

  @Action
  public void nullMethod() {

  }
}
